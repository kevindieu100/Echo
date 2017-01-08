package com.echo.echo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class bluetooth {

    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int s;

    public static final int no = 0;
    public static final int listen = 1;
    public static final int connecting = 2;
    public static final int connected = 3;
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    public bluetooth(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        s = no;
        this.handler = handler;
    }

    private synchronized void setState(int state) {
        s = state;
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return s;
    }

    public synchronized void start() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(listen);
        if (acceptThread == null) {
            acceptThread = new AcceptThread(false);
            acceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (s == connecting) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(connecting);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, String socketType) {

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(connected);
    }

    public synchronized void stop() {

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(no);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (s != connected) return;
            r = connectedThread;
        }

        r.write(out);
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);

        bluetooth.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        bluetooth.this.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private String Stype;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            Stype = secure ? "Secure" : "Insecure";

            try {
                    tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
            } catch (IOException e) {
            }
            serverSocket = tmp;
        }

        public void run() {
            setName("AcceptThread" + Stype);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (s != connected) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();
                } catch (IOException e) {
               //     Log.e(TAG, "Socket Type: " + Stype + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (bluetooth.this) {
                        switch (s) {
                            case listen:
                            case connecting:
                                connected(socket, socket.getRemoteDevice(),Stype);
                                break;
                            case no:
                            case connected:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                //    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private String Stype;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            this.device = device;
            BluetoothSocket tmp = null;
            Stype = "Insecure";

            try {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
            }
            socket = tmp;
        }

        public void run() {
            setName("ConnectThread" + Stype);

            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                }
                connectionFailed();
                return;
            }

            synchronized (bluetooth.this) {
                connectThread = null;
            }

            connected(socket, device, Stype);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (s == connected) {
                try {
                    bytes = mmInStream.read(buffer);

                    handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    bluetooth.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {

            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
