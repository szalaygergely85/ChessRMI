/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessrmi;

/**
 *
 * @author Gege
 */
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerService extends UnicastRemoteObject implements ChessService, java.io.Serializable {

    public ChessPlayers serverPl;
    public ChessPlayers clientPl;
    ChessBoard[][] board;
    int xA, yA, oldX, oldY;
    public String sPlCl;
    ClientService client;
    BoardCanvas canvas;
    GameWindow sWindow;

    @Override
    public String getLabel() throws RemoteException {

        return sWindow.jLabel3.getText();
    }

    @Override
    public void setLabel(String newT) throws RemoteException {
     String oldtext=sWindow.jLabel3.getText();
     newT=oldtext + newT;
     sWindow.jLabel3.setText(newT);
     client.RefreshChat();
     sWindow.window.revalidate();
    }
    @Override
     public void setLabel(String newT, int elso) throws RemoteException {
     String oldtext=sWindow.jLabel3.getText();
     newT=oldtext + newT;
     sWindow.jLabel3.setText(newT);
     sWindow.window.revalidate();
    }
    public static void clearField(ChessBoard[][] board) {
        for (int z = 0; z < 8; z++) {
            for (int w = 0; w < 8; w++) {

                board[z][w].active = false;
                board[z][w].attackable = false;
                board[z][w].steppable = false;
            }
        }

    }

    @Override
    public void makeStepp(int activeX, int activeY, int xA, int yA) throws RemoteException {

        board[activeX][activeY].ColorFigure = board[xA][yA].ColorFigure;
        board[activeX][activeY].NameFigure = board[xA][yA].NameFigure;
        board[activeX][activeY].figureId = board[xA][yA].figureId;
        board[xA][yA].active = false;
        board[xA][yA].ColorFigure = "";
        board[xA][yA].NameFigure = "";
        board[xA][yA].figureId = 100;

        canvas.clearC();
        serverPl.next = !serverPl.next;
    }

    @Override
    public String createClientPlayer(String namePl) throws RemoteException {
        if (serverPl.playerColor == "Black") {
            clientPl = new ChessPlayers(namePl, "White");
            return "White";
        } else {
            clientPl = new ChessPlayers(namePl, "Black");
            return "Black";
        }
    }

    @Override
    public String getPlayerName() throws RemoteException {
        String name3 = serverPl.name;
        return name3;
    }

    @Override
    public String getColorF(int x, int y) throws RemoteException {
        return board[x][y].ColorFigure;
    }

    @Override
    public String getNameF(int x, int y) throws RemoteException {
        return board[x][y].NameFigure;
    }

    @Override
    public boolean PlayerNext() throws RemoteException {
        boolean next = serverPl.next;

        return next;
    }

    @Override
    public int getFID(int x, int y) throws RemoteException {
        return board[x][y].figureId;
    }

    @Override
    public void ConnectClient(ClientService n) throws RemoteException {
        client = (ClientService) n;
        client.RefreshClient();
    }

    @Override
    public String getColorField(int x, int y) throws RemoteException {
        return board[x][y].colorField;
    }

    @Override
    public boolean getActive(int x, int y) throws RemoteException {
        return board[x][y].active;
    }

    public boolean getAttackable(int x, int y) throws RemoteException {
        return board[x][y].attackable;
    }

    public boolean getSteppable(int x, int y) throws RemoteException {
        return board[x][y].steppable;
    }

    ChessBoard[][] createBoard(String plColor) {
        ChessBoard[][] board = new ChessBoard[8][8];
        for (int z = 0; z < 8; z++) {
            for (int w = 0; w < 8; w++) {

                board[z][w] = new ChessBoard("", 100);
                if ((z + w) % 2 != 0) {
                    board[z][w].colorField = "Dark";

                }
            }
        }

        int x = 0;
        int y = 0;
        String name = "";
        String colorF = "";
        for (int i = 0; i < 32; i++) {
            if (i < 16) {
                name = "Pawn";
                if (i < 8) {
                    y = 7;
                    x = i + 1;
                    colorF = "White";
                } else {
                    y = 2;
                    x = i - 7;
                    colorF = "Black";
                }

            } else if (i < 20) {
                name = "Rock";
                if (i < 18) {
                    y = 8;
                    x = i % 16 * 7 + 1;
                    colorF = "White";
                } else {
                    y = 1;
                    x = i % 18 * 7 + 1;
                    colorF = "Black";
                }

            } else if (i < 24) {
                name = "Knight";
                if (i < 22) {
                    y = 8;
                    x = i % 20 * 5 + 2;
                    colorF = "White";
                } else {
                    y = 1;
                    x = i % 22 * 5 + 2;
                    colorF = "Black";
                }

            } else if (i < 28) {
                name = "Bishop";
                if (i < 26) {
                    y = 8;
                    x = i % 24 * 3 + 3;
                    colorF = "White";
                } else {
                    y = 1;
                    x = i % 26 * 3 + 3;
                    colorF = "Black";
                }

            } else if (i < 30) {
                name = "Queen";
                if (i < 29) {
                    y = 8;
                    x = 4;
                    colorF = "White";
                } else {
                    y = 1;
                    x = 4;
                    colorF = "Black";
                }

            } else {
                name = "King";
                if (i < 31) {
                    y = 8;
                    x = 5;
                    colorF = "White";
                } else {
                    y = 1;
                    x = 5;
                    colorF = "Black";
                }

            }

            board[x - 1][y - 1].NameFigure = name;
            board[x - 1][y - 1].ColorFigure = colorF;
            board[x - 1][y - 1].figureId = i;
        }
        return board;
    }

    public void Steppes(int activeX, int activeY, ChessBoard[][] board) {
        int x = activeX;
        int y = activeY;
        if ("Pawn".equals(board[x][y].NameFigure)) {
            if ("Black".equals(sPlCl)) {
                if (board[x][y + 1].figureId == 100) {
                    if (y == 1) {
                        if (board[x][y + 2].figureId == 100) {
                            board[x][y + 2].steppable = true;
                        }
                    }

                    board[x][y + 1].steppable = true;
                }
                if (x > 0) {
                    System.out.println(board[x - 1][y + 1].ColorFigure);
                    if ("White".equals(board[x - 1][y + 1].ColorFigure)) {
                        board[x - 1][y + 1].attackable = true;

                    }
                }
                if (x < 7) {
                    System.out.println(board[x + 1][y + 1].ColorFigure);
                    if ("White".equals(board[x + 1][y + 1].ColorFigure)) {
                        board[x + 1][y + 1].attackable = true;

                    }
                }

            } else {
                if (board[x][y - 1].figureId == 100) {
                    if (y == 6) {
                        if (board[x][y - 2].figureId == 100) {
                            board[x][y - 2].steppable = true;
                        }
                    }
                    board[x][y - 1].steppable = true;
                }
                if (x > 0) {
                    if ("Black".equals(board[x - 1][y - 1].ColorFigure)) {
                        board[x - 1][y - 1].attackable = true;
                    }
                }
                if (x < 7) {
                    if ("Black".equals(board[x + 1][y - 1].ColorFigure)) {
                        board[x + 1][y - 1].attackable = true;
                    }
                }
            }
        }

        if ("Rock".equals(board[x][y].NameFigure)) {
            int i = x;
            int j = y;
            if ("Black".equals(sPlCl)) {
                if (i != 7) {
                    i++;
                    while (i < 8 && board[i][y].figureId == 100) {
                        board[i][y].steppable = true;

                        i++;
                    }
                    if (i < 8) {
                        if ("White".equals(board[i][y].ColorFigure)) {
                            board[i][y].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (i != 0) {
                    i--;
                    while (i >= 0 && board[i][y].figureId == 100) {
                        board[i][y].steppable = true;

                        i--;
                    }
                    if (i >= 0) {
                        if ("White".equals(board[i][y].ColorFigure)) {
                            board[i][y].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (j != 7) {
                    j++;
                    while (j < 8 && board[x][j].figureId == 100) {
                        board[x][j].steppable = true;

                        j++;
                    }
                    if (j < 8) {
                        if ("White".equals(board[x][j].ColorFigure)) {
                            board[x][j].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (j != 0) {
                    j--;
                    while (j >= 0 && board[x][j].figureId == 100) {
                        board[x][j].steppable = true;

                        j--;
                    }
                    if (j >= 0) {
                        if ("White".equals(board[x][j].ColorFigure)) {
                            board[x][j].attackable = true;
                        }
                    }
                }
            } else {
                if (i != 7) {
                    i++;
                    while (i < 8 && board[i][y].figureId == 100) {
                        board[i][y].steppable = true;

                        i++;
                    }
                    if (i <= 7) {
                        if ("Black".equals(board[i][y].ColorFigure)) {
                            board[i][y].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (i != 0) {
                    i--;
                    while (i >= 0 && board[i][y].figureId == 100) {
                        board[i][y].steppable = true;

                        i--;
                    }
                    if (i >= 0) {
                        if ("Black".equals(board[i][y].ColorFigure)) {
                            board[i][y].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (j != 7) {
                    j++;
                    while (j < 8 && board[x][j].figureId == 100) {
                        board[x][j].steppable = true;

                        j++;
                    }
                    if (j < 8) {
                        if ("Black".equals(board[x][j].ColorFigure)) {
                            board[x][j].attackable = true;
                        }
                    }
                }
                i = x;
                j = y;
                if (j != 0) {
                    j--;
                    while (j >= 0 && board[x][j].figureId == 100) {
                        board[x][j].steppable = true;

                        j--;
                    }
                    if (j >= 0) {
                        if ("Black".equals(board[x][j].ColorFigure)) {
                            board[x][j].attackable = true;
                        }
                    }
                }
            }
        }
        if ("Knight".equals(board[x][y].NameFigure)) {
            int i = x;
            int j = y;
            i += 1;
            j += 2;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i += 2;
            j += 1;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i -= 2;
            j += 1;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i += 2;
            j -= 1;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i -= 1;
            j += 2;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i += 1;
            j -= 2;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i -= 1;
            j -= 2;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }
            i = x;
            j = y;
            i -= 2;
            j -= 1;
            if (i >= 0 && j >= 0 && j < 8 && i < 8) {
                if (board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                } else if (!sPlCl.equals(board[i][j].ColorFigure)) {
                    board[i][j].attackable = true;

                }
            }

        }
        if ("Bishop".equals(board[x][y].NameFigure)) {
            int i = x;
            int j = y;
            System.out.println(i + " " + j);
            if (i != 7 && j != 7) {
                i++;
                j++;
                while (i < 8 && j < 8 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j++;
                    i++;
                }
                if (i < 8 && j < 8) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (i != 7 && j != 0) {
                i++;
                j--;
                while (i < 8 && j >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j--;
                    i++;
                }
                if (i < 8 && j >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (j != 7 && i != 0) {
                i--;
                j++;
                while (j < 8 && i >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    i--;
                    j++;
                }
                if (j < 8 && i >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (i != 0 && j != 0) {
                i--;
                j--;
                while (i >= 0 && j >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j--;
                    i--;
                }
                if (i >= 0 && j >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }

        }
        if ("Queen".equals(board[x][y].NameFigure)) {
            int i = x;
            int j = y;
            System.out.println(i + " " + j);
            if (i != 7 && j != 7) {
                i++;
                j++;
                while (i < 8 && j < 8 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j++;
                    i++;
                }
                if (i < 8 && j < 8) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (i != 7 && j != 0) {
                i++;
                j--;
                while (i < 8 && j >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j--;
                    i++;
                }
                if (i < 8 && j >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (j != 7 && i != 0) {
                i--;
                j++;
                while (j < 8 && i >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    i--;
                    j++;
                }
                if (j < 8 && i >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            System.out.println(i + " " + j);
            if (i != 0 && j != 0) {
                i--;
                j--;
                while (i >= 0 && j >= 0 && board[i][j].figureId == 100) {
                    board[i][j].steppable = true;
                    j--;
                    i--;
                }
                if (i >= 0 && j >= 0) {
                    if (!sPlCl.equals(board[i][j].ColorFigure)) {
                        board[i][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            if (i != 7) {
                i++;
                while (i < 8 && board[i][y].figureId == 100) {
                    board[i][y].steppable = true;

                    i++;
                }
                if (i <= 7) {
                    if (!sPlCl.equals(board[i][y].ColorFigure)) {
                        board[i][y].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            if (i != 0) {
                i--;
                while (i >= 0 && board[i][y].figureId == 100) {
                    board[i][y].steppable = true;

                    i--;
                }
                if (i >= 0) {
                    if (!sPlCl.equals(board[i][y].ColorFigure)) {
                        board[i][y].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            if (j != 7) {
                j++;
                while (j < 8 && board[x][j].figureId == 100) {
                    board[x][j].steppable = true;

                    j++;
                }
                if (j < 8) {
                    if (!sPlCl.equals(board[x][j].ColorFigure)) {
                        board[x][j].attackable = true;
                    }
                }
            }
            i = x;
            j = y;
            if (j != 0) {
                j--;
                while (j >= 0 && board[x][j].figureId == 100) {
                    board[x][j].steppable = true;

                    j--;
                }
                if (j >= 0) {
                    if (!sPlCl.equals(board[x][j].ColorFigure)) {
                        board[x][j].attackable = true;
                    }
                }
            }

        }
        if ("King".equals(board[x][y].NameFigure)) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (((x + i) > 0) && ((x + i) < 8) && ((y + j) > 0) && ((y + j) < 8)) {
                        System.out.println();
                        if (board[x + i][y + j].figureId == 100) {
                            board[x + i][y + j].steppable = true;
                        }
                        if (!sPlCl.equals(board[x + i][y + j].ColorFigure)) {
                            board[x + i][y + j].attackable = true;
                        }
                    }
                }
            }

        }

    }
    private void SendBActionPerformed(java.awt.event.ActionEvent evt) { 
        try {
             DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
   LocalDateTime now = LocalDateTime.now();
  
            setLabel("<html><br><b>"+serverPl.name+"</b> <i>"+ dtf.format(now)+":</i> " + sWindow.textF.getText());
            sWindow.textF.setText("");
            
        } catch (RemoteException ex) {
            Logger.getLogger(ServerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public ServerService(String namePl) throws RemoteException, UnknownHostException {
        serverPl = new ChessPlayers(namePl);
        sWindow = new GameWindow();
        board = (ChessBoard[][]) createBoard(serverPl.playerColor);
        canvas = new BoardCanvas(board, serverPl.playerColor);
        sWindow.AddCanvas(canvas);
        InetAddress localhost = InetAddress.getLocalHost();
        String myIp = (localhost.getHostAddress()).trim();
        sWindow.jLabel3.setText("<html><b>Hi " + namePl + ", you are with the " + serverPl.playerColor + " color and your IP address is: " + myIp);
        sWindow.window.revalidate();
        sWindow.send2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendBActionPerformed(evt);
            }
        });
        sWindow.window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                sPlCl = serverPl.playerColor;
                int activeX = (e.getX() - 8) / 100;
                int activeY = (e.getY() - 31) / 100;
                xA = activeX - 1;
                yA = activeY - 1;
                if ("Black".equals(serverPl.playerColor)) {
                    xA = 7 - xA;
                    yA = 7 - yA;
                }
                if (serverPl.next) {
                    //activate figures
                    if (canvas.board[xA][yA].ColorFigure.equals(sPlCl)) {

                        clearField(board);
                        oldX = xA;
                        oldY = yA;
                        board[xA][yA].active = true;
                        Steppes(xA, yA, board);

                        canvas.clearC();

                    }
                    //make a stepp
                    if (board[xA][yA].steppable == true || board[xA][yA].attackable == true) {
                        clearField(board);
                        try {
                            //     System.out.println(oldX + " " + oldY + " " + xA + " " + yA);
                            makeStepp(xA, yA, oldX, oldY);
                            canvas.clearC();
                        } catch (RemoteException ex) {
                            Logger.getLogger(ClientSide.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            client.RefreshClient();
                        } catch (RemoteException ex) {
                            Logger.getLogger(ServerService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //click on an empty field
                    if (board[xA][yA].active == false && board[xA][yA].steppable == false) {
                        clearField(board);
                        canvas.clearC();
                    }
                }
            }
        });

    }
}
