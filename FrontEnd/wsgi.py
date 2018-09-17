from Server_GitHub import application

if __name__ == "__main__":
    while True:
        try:
            application.run()
        except Exception as e:
            print e
