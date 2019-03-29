#!/usr/bin/env python3
import logging
import socket
import threading
import sys
import time
import traceback

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logging.getLogger().addHandler(logging.StreamHandler())

PORT = 3000
BUFFER_SIZE = 1024
IP = '127.0.0.1'


def getInputs(clientsocket):
    try:
        server_msg = clientsocket.recv(BUFFER_SIZE).decode()
        return server_msg
    except KeyboardInterrupt:
        pass


def main():
    logging.info("Starting the client")
    time.sleep(.002)
    clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        clientsocket.connect((IP, PORT))
    except:
        print("Couldn't connect to the server.")
        return

    clientsocket.settimeout(1)

    loop = True

    while loop:
        msg = getInputs(clientsocket)
        if "SERVERINPUT#" in msg:
            a, question = msg.split('#')
            msg = input(str(question))
            clientsocket.send(msg.encode())
        if "SERVEROK#" in msg:
            a, message = msg.split('#')
            loop = False

    Thread1 = readThread(clientsocket)
    Thread1.start()

    try:
        broken = False
        while True:
            time.sleep(.1)
            msg = input('Type a message to be send: ')
            try:
                clientsocket.send(msg.encode())
            except BrokenPipeError:
                print("Server offline, shuttingdown this connection feed")
                broken = True
                return
    except KeyboardInterrupt:
        pass
    finally:
        Thread1.stop()
        logging.info("Stopping the client")
        msg = "SERVEROK#QUIT"
        if not broken:
            clientsocket.send(msg.encode())
            clientsocket.close()

        time.sleep(.1)


class readThread(threading.Thread):
    def __init__(self, clientsocket):
        super(readThread, self).__init__()
        self.clientsocket = clientsocket
        self.event = threading.Event()

    def getInputs(self, clientsocket):
        try:
            serverMsg = clientsocket.recv(BUFFER_SIZE).decode()
            return serverMsg
        except KeyboardInterrupt:
            print("Das jammer")

    def run(self):
        while not self.event.is_set():
            try:
                message = self.getInputs(self.clientsocket)

                if message is not None:
                    print(message)
            except Exception as e:
                pass

    def stop(self):
        self.event.clear()
        self.event.set()


if __name__ == "__main__":
    main()

    while True:
        try:
            inp = input("Do you want to reconnect? [Y/N]")

            if inp == "y" or inp == "Y":
                main()

            if inp == "n" or inp == "N":
                break
        except KeyboardInterrupt:
            print("Goodbye")
    sys.exit()
