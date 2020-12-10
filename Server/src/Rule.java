import static java.lang.StrictMath.abs;

public class Rule
{
    //Xét đánh được
    public static boolean Rule1(int x1, int y1, int x2, int y2, int[][] piece)
    {
        return Rule(x1, y1, x2, y2, piece) && !Chieu(x1, y1, x2, y2, piece);
    }
    public static boolean Rule2(int x1, int y1, int x2, int y2, int[][] piece)
    {
        int[][] apiece = new int[10][9];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++)
                apiece[i][j] = -piece[9 - i][j];
        int ax1 = 9 - x1, ax2 = 9 - x2;
        return Rule(ax1, y1, ax2, y2, apiece) && !Chieu(ax1, y1, ax2, y2, apiece);
    }

    //Xét cờ
    public static boolean Rule(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (piece[x2][y2] > 0) return false;
        if (x1 == x2 && y1 == y2) return false;
        if (piece[x1][y1] == 1) return Xa(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 2) return Ma(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 3) return Tuongj(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 4) return Si(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 5) return Tuong(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 6) return Phao(x1, y1, x2, y2, piece);
        if (piece[x1][y1] == 7) return Tot(x1, y1, x2, y2, piece);
        return false;
    }

    //Xét chiếu
    public static boolean Chieu(int x1, int y1, int x2, int y2, int[][] piece)
    {
        int[][] apiece = new int[10][9];
        int ax1 = 9 - x1, ax2 = 9 - x2, xt = 0, yt = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++) apiece[i][j] = -piece[9 - i][j];
        apiece[ax2][y2] = apiece[ax1][y1];
        apiece[ax1][y1] = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++)
                if (apiece[i][j] == -5)
                {
                    xt = i;
                    yt = j;
                }
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++)
            {
                if (apiece[i][j] == 1 && Xa(i, j, xt, yt, apiece)) return true;
                if (apiece[i][j] == 2 && Ma(i, j, xt, yt, apiece)) return true;
                if (apiece[i][j] == 6 && Phao(i, j, xt, yt, apiece)) return true;
                if (apiece[i][j] == 7 && Tot(i, j, xt, yt, apiece)) return true;
                if (apiece[i][j] == 5 && j == yt)
                {
                    boolean d = false;
                    for (int k = i - 1; k > xt; k--) if (apiece[k][j] != 0) d = true;
                    if (!d) return true;
                }
            }
        return false;
    }

    //Xét chiếu hết
    public static boolean Chieuhet(int[][] piece, String plr)
    {
        int[][] apiece = new int[10][9];
        if (plr.equals("1"))
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 9; j++) apiece[i][j] = -piece[9 - i][j];
        else
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 9; j++) apiece[i][j] = piece[i][j];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 9; j++)
                if (apiece[i][j] > 0)
                {
                    for (int i1 = 0; i1 < 10; i1++)
                        for (int j1 = 0; j1 < 9; j1++)
                            if (Rule(i, j, i1, j1, apiece))
                            {
                                int[][] bpiece = new int[10][9], cpiece = new int[10][9];
                                int xt = 0, yt = 0;
                                for (int i2 = 0; i2 < 10; i2++)
                                    for (int j2 = 0; j2 < 9; j2++) bpiece[i2][j2] = apiece[i2][j2];
                                bpiece[i1][j1] = bpiece[i][j];
                                bpiece[i][j] = 0;
                                for (int i2 = 0; i2 < 10; i2++)
                                    for (int j2 = 0; j2 < 9; j2++) cpiece[i2][j2] = -bpiece[9 - i2][j2];
                                for (int i2 = 0; i2 < 10; i2++)
                                    for (int j2 = 0; j2 < 9; j2++)
                                        if (cpiece[i2][j2] == -5)
                                        {
                                            xt = i2;
                                            yt = j2;
                                        }
                                boolean d = false;
                                for (int i2 = 0; i2 < 10; i2++)
                                    for (int j2 = 0; j2 < 9; j2++)
                                    {
                                        if (cpiece[i2][j2] == 1 && Xa(i2, j2, xt, yt, cpiece)) d = true;
                                        if (cpiece[i2][j2] == 2 && Ma(i2, j2, xt, yt, cpiece)) d = true;
                                        if (cpiece[i2][j2] == 6 && Phao(i2, j2, xt, yt, cpiece)) d = true;
                                        if (cpiece[i2][j2] == 7 && Tot(i2, j2, xt, yt, cpiece)) d = true;
                                        if (cpiece[i2][j2] == 5 && j2 == yt)
                                        {
                                            boolean di = false;
                                            for (int k = i2 - 1; k > xt; k--) if (cpiece[k][j2] != 0) di = true;
                                            if (!di) d = true;
                                        }
                                    }
                                if (!d) return false;
                            }
                }
        return true;
    }

    //Xa
    public static boolean Xa(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x1 != x2 && y1 != y2) return false;
        if (x1 == x2)
        {
            if (y1 < y2) for (int i = y1 + 1; i < y2; i++) if (piece[x1][i] != 0) return false;
            if (y1 > y2) for (int i = y1 - 1; i > y2; i--) if (piece[x1][i] != 0) return false;
        }
        if (y1 == y2)
        {
            if (x1 < x2) for (int i = x1 + 1; i < x2; i++) if (piece[i][y1] != 0) return false;
            if (x1 > x2) for (int i = x1 - 1; i > x2; i--) if (piece[i][y2] != 0) return false;
        }
        return true;
    }

    //Mã
    public static boolean Ma(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (!(abs(x1 - x2) == 1 && abs(y1 - y2) == 2) && !(abs(x1 - x2) == 2 && abs(y1 - y2) == 1)) return false;
        if (x1 - x2 == 2) if (piece[x1 - 1][y1] != 0) return false;
        if (x2 - x1 == 2) if (piece[x1 + 1][y1] != 0) return false;
        if (y1 - y2 == 2) if (piece[x1][y1 - 1] != 0) return false;
        if (y2 - y1 == 2) if (piece[x1][y1 + 1] != 0) return false;
        return true;
    }

    //Tượng
    public static boolean Tuongj(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x2 < 5) return false;
        if (!(abs(x1 - x2) == 2 && abs(y1 - y2) == 2)) return false;
        if (x1 - x2 == 2 && y1 - y2 == 2) if (piece[x1 - 1][y1 - 1] != 0) return false;
        if (x2 - x1 == 2 && y1 - y2 == 2) if (piece[x1 + 1][y1 - 1] != 0) return false;
        if (x1 - x2 == 2 && y2 - y1 == 2) if (piece[x1 - 1][y1 + 1] != 0) return false;
        if (x2 - x1 == 2 && y2 - y1 == 2) if (piece[x1 + 1][y1 + 1] != 0) return false;
        return true;
    }

    //Sĩ
    public static boolean Si(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x2 < 7 || y2 < 3 || y2 > 5) return false;
        if (!(abs(x1 - x2) == 1 && abs(y1 - y2) == 1)) return false;
        return true;
    }

    //Tướng
    public static boolean Tuong(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x2 < 7 || y2 < 3 || y2 > 5) return false;
        if (!(x1 == x2 && abs(y1 - y2) == 1) && !(abs(x1 - x2) == 1 && y1 == y2)) return false;
        return true;
    }

    //Pháo
    public static boolean Phao(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x1 != x2 && y1 != y2) return false;
        if (piece[x2][y2] == 0)
        {
            if (x1 == x2)
            {
                if (y1 < y2) for (int i = y1 + 1; i < y2; i++) if (piece[x1][i] != 0) return false;
                if (y1 > y2) for (int i = y1 - 1; i > y2; i--) if (piece[x1][i] != 0) return false;
            }
            if (y1 == y2)
            {
                if (x1 < x2) for (int i = x1 + 1; i < x2; i++) if (piece[i][y1] != 0) return false;
                if (x1 > x2) for (int i = x1 - 1; i > x2; i--) if (piece[i][y2] != 0) return false;
            }
        }   else
        {
            int d = 0;
            if (x1 == x2)
            {
                if (y1 < y2) for (int i = y1 + 1; i < y2; i++) if (piece[x1][i] != 0) d++;
                if (y1 > y2) for (int i = y1 - 1; i > y2; i--) if (piece[x1][i] != 0) d++;
            }
            if (y1 == y2)
            {
                if (x1 < x2) for (int i = x1 + 1; i < x2; i++) if (piece[i][y1] != 0) d++;
                if (x1 > x2) for (int i = x1 - 1; i > x2; i--) if (piece[i][y2] != 0) d++;
            }
            if (d != 1) return false;
        }
        return true;
    }

    //Tốt
    public static boolean Tot(int x1, int y1, int x2, int y2, int[][] piece)
    {
        if (x1 > 4)
        {
            if (!(x1 - x2 == 1 && y1 == y2)) return false;
            return true;
        }
        if (!(x1 - x2 == 1 && y1 == y2) && !(x1 == x2 && abs(y1 - y2) == 1)) return false;
        return true;
    }
}
