import mysql.connector

def create_connector(password,host="localhost",port=3306,user = "root",start_from_cursor = False):
    cnx = mysql.connector.connect(
    host="127.0.0.1",
    port=3306,
    user="mike",
    password="s3cre3t!")

    #start_from_cursor allows to get directly query object,the rest of parameters are basic mySql connection stuff
    if start_from_cursor:
        return cnx.cursor()
    else:
        return cnx

