package net.socialhub.j2objc.security;

/*
 * Copyright (c) 2002, 2009, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * HMAC-SHA1 256 384 512 algorithms
 * See RFC 2104 for spec
 */
final class HmacCore implements Cloneable {

    private final MessageDigest md;

    /** inner padding - key XORd with ipad */
    private final byte[] k_ipad;

    /** outer padding - key XORd with opad */
    private final byte[] k_opad; //

    /** Is this the first data to be processed ? */
    private boolean first;

    private final int blockLen;

    HmacCore(MessageDigest md, int bl) {
        this.md = md;
        this.blockLen = bl;
        this.k_ipad = new byte[blockLen];
        this.k_opad = new byte[blockLen];
        first = true;
    }

    HmacCore(String digestAlgorithm, int bl) throws NoSuchAlgorithmException {
        this(MessageDigest.getInstance(digestAlgorithm), bl);
    }

    private HmacCore(HmacCore other) throws CloneNotSupportedException {
        this.md = (MessageDigest) other.md.clone();
        this.blockLen = other.blockLen;
        this.k_ipad = other.k_ipad.clone();
        this.k_opad = other.k_opad.clone();
        this.first = other.first;
    }

    int getDigestLength() {
        return this.md.getDigestLength();
    }

    void init(Key key, AlgorithmParameterSpec params)
            throws InvalidKeyException, InvalidAlgorithmParameterException {

        if (params != null) {
            throw new InvalidAlgorithmParameterException
                    ("HMAC does not use parameters");
        }

        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Secret key expected");
        }

        byte[] secret = key.getEncoded();
        if (secret == null) {
            throw new InvalidKeyException("Missing key data");
        }

        // if key is longer than the block length,
        // reset it using the message digest object.
        if (secret.length > blockLen) {
            byte[] tmp = md.digest(secret);
            Arrays.fill(secret, (byte) 0);
            secret = tmp;
            md.reset();
        }

        // XOR k with ipad and opad, respectively
        for (int i = 0; i < blockLen; i++) {
            int si = (i < secret.length) ? secret[i] : 0;
            k_ipad[i] = (byte) (si ^ 0x36);
            k_opad[i] = (byte) (si ^ 0x5c);
        }

        // now erase the secret
        Arrays.fill(secret, (byte) 0);
        secret = null;

        reset();
    }

    void update(byte input) {
        if (first) {
            md.update(k_ipad);
            first = false;
        }

        md.update(input);
    }

    void update(byte input[], int offset, int len) {
        if (first) {
            md.update(k_ipad);
            first = false;
        }

        md.update(input, offset, len);
    }

    void update(ByteBuffer input) {
        if (first) {
            md.update(k_ipad);
            first = false;
        }

        md.update(input);
    }

    byte[] doFinal() {
        if (first) {
            md.update(k_ipad);
        } else {
            first = true;
        }

        try {
            // finish the inner digest
            byte[] tmp = md.digest();
            md.reset();

            // compute digest for 2nd pass
            // start with outer pad
            md.update(k_opad);
            // add result of 1st hash
            md.update(tmp);

            md.digest(tmp, 0, tmp.length);
            md.reset();
            return tmp;

        } catch (DigestException e) {
            throw new ProviderException(e);
        }
    }

    void reset() {
        if (!first) {
            md.reset();
            first = true;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return new HmacCore(this);
    }


    public static class HmacBase extends MacSpi implements Cloneable {

        private HmacCore hmac = null;

        public HmacBase(MessageDigest md, int blockLength) throws NoSuchAlgorithmException {
            this.hmac = new HmacCore(md, blockLength);
        }

        private HmacBase(HmacBase base) throws CloneNotSupportedException {
            this.hmac = (HmacCore) base.hmac.clone();
        }

        @Override
        protected int engineGetMacLength() {
            return hmac.getDigestLength();
        }

        @Override
        protected void engineInit(Key key, AlgorithmParameterSpec params)
                throws InvalidKeyException, InvalidAlgorithmParameterException {
            hmac.init(key, params);
        }

        @Override
        protected void engineUpdate(byte input) {
            hmac.update(input);
        }

        @Override
        protected void engineUpdate(byte input[], int offset, int len) {
            hmac.update(input, offset, len);
        }

        @Override
        protected void engineUpdate(ByteBuffer input) {
            hmac.update(input);
        }

        @Override
        protected byte[] engineDoFinal() {
            return hmac.doFinal();
        }

        @Override
        protected void engineReset() {
            hmac.reset();
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new HmacBase(this);
        }
    }

    public static final class HmacSHA1 extends HmacBase {
        public HmacSHA1() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-1"), 64);
        }
    }

    public static final class HmacSHA256 extends HmacBase {
        public HmacSHA256() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-256"), 64);
        }
    }

    public static final class HmacSHA384 extends HmacBase {
        public HmacSHA384() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-384"), 128);
        }
    }

    public static final class HmacSHA512 extends HmacBase {
        public HmacSHA512() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-512"), 128);
        }
    }
}