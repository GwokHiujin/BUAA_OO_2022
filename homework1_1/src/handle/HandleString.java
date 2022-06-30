package handle;

public class HandleString {
    private String ans;

    public HandleString(String ans) {
        this.ans = ans;
    }

    public String getAns() {
        return ans;
    }

    public void trimPlus() {
        if (this.ans.charAt(0) == '+') {
            this.ans = this.ans.substring(1);
        }
        if (this.ans.charAt(ans.length() - 1) == '+') {
            this.ans = this.ans.substring(0, this.ans.length() - 1);
        }
    }

    public void shorten() {
        if (ans.length() == 0) {
            ans = "0";
        }
        if (ans.charAt(0) == '0' && ans.length() > 1 && !Character.isDigit(ans.charAt(1))) {
            ans = ans.substring(1);
        }
        trimPlus();

        ans = ans.replaceAll("\\+-", "-");
        ans = ans.replaceAll("x\\*\\*2", "x*x");
        if (ans.charAt(0) == '-') {
            int k = 0;
            while (k < ans.length() && ans.charAt(k) != '+') {
                k++;
            }
            if (k < (ans.length() - 1)) {
                String str1 = ans.substring(0, k);
                String str2 = ans.substring(k + 1);
                ans = str2 + str1;
            }
        }
    }

    public void handleSign() {
        StringBuilder sb = new StringBuilder();
        //递归处理每一组正负号
        for (int i = 0; i < this.ans.length(); i++) {
            if (this.ans.charAt(i) == '+' || this.ans.charAt(i) == '-') {
                StringBuilder sign = new StringBuilder();
                //get a string of sign
                while ((this.ans.charAt(i) == '+' || this.ans.charAt(i) == '-') &&
                        i < this.ans.length()) {
                    sign.append(this.ans.charAt(i));
                    i++;
                }
                i--;
                for (int k = sign.length() - 1; k > 0; k--) {
                    sign.setCharAt(k - 1, calSign(sign.charAt(k), sign.charAt(k - 1)));
                }
                sb.append(sign.charAt(0));
            }
            else {
                sb.append(this.ans.charAt(i));
            }
        }
        this.ans = sb.toString().replaceAll("-", "+-").replaceAll("-x", "-1*x");
    }

    public Character calSign(Character a, Character b) {
        if (a == '+' && b == '-') {
            return '-';
        }
        else if (a == '+' && b == '+') {
            return '+';
        }
        else if (a == '-' && b == '-') {
            return '+';
        }
        else if (a == '-' && b == '+') {
            return '-';
        }
        else {
            return '+';
        }
    }

    public void handleFS() {
        if (this.ans.startsWith("+-")) {
            this.ans = this.ans.substring(1);
        }
        if (this.ans.charAt(0) == '-') {
            this.ans = "0+" + this.ans;
        }
        if (this.ans.charAt(0) == '+') {
            this.ans = this.ans.substring(1);
        }
    }

    public void simplifyOp() {
        this.ans = this.ans.replaceAll("\\*\\+", "*");
        this.ans = this.ans.replaceAll("\\^\\+", "^");

        //---------------Change [(+] to [(], [(-] to [(0+-], [-(] to [-1*(]---------------
        this.ans = this.ans.replaceAll("\\(\\+", "(");
        this.ans = this.ans.replaceAll("\\(-", "(0+-");
        this.ans = this.ans.replaceAll("-\\(", "-1*(");
    }
}
