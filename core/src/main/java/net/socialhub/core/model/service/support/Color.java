package net.socialhub.core.model.service.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {

    private int r;
    private int g;
    private int b;
    private int a;

    /**
     * Initialize with white color.
     */
    public Color() {
        r = 255;
        g = 255;
        b = 255;
        a = 255;
    }

    /**
     * Initialize with JavaScript expression.
     */
    public Color(String javaScriptColorExpression) {
        Pattern p = Pattern.compile("rgb\\(([0-9]+),([0-9]+),([0-9]+)\\)");
        Matcher m = p.matcher(javaScriptColorExpression);

        if (m.find()) {
            r = Integer.parseInt(m.group(1));
            g = Integer.parseInt(m.group(2));
            b = Integer.parseInt(m.group(3));
            a = 255;
        }
    }

    /**
     * Get JavaScript Color Format
     * JavaScript で扱う色フォーマットに変換
     */
    public String toJavaScriptFormat() {
        return "rgb(" + r + "," + g + "," + b + ")";
    }

    // region
    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
    // endregion
}