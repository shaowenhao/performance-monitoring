from sqlalchemy import MetaData, create_engine
from sqlalchemy.exc import OperationalError
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from automation.core.components.database.base_database import BaseDatabase
import sys, importlib
importlib.reload(sys)
# sys.setdefaultencoding('utf8')
import os
os.environ['NLS_LANG'] = 'SIMPLIFIED CHINESE_CHINA.UTF8'

class Oracle(BaseDatabase):
    def __init__(self, db_username, db_password, hostname, port, db_name):
        self.hostname = hostname
        self.port = port
        self.db_username = db_username
        self.db_password = db_password
        self.db_name = db_name

    def _connect(self, stop_on_error=False, collect_report=True):
        """Connect to db
        Returns:
            engine, session, meta
        """
        # We connect with the help of the PostgreSQL URL
        # postgresql://federer:[email protected]:5432/tennis
        url = 'oracle://{}:{}@{}:{}/{}'
        url = url.format(self.db_username, self.db_password, self.hostname, self.port, self.db_name)

        try:
            # The return value of create_engine() is our connection object
            engine = create_engine(url)

            Session = sessionmaker(bind=engine)
            session = Session()
            # We then bind the connection to MetaData()
            meta = MetaData(engine, reflect=True)
        except OperationalError as e:
            #             print('there was error when connecting DB, error msg is: {}.'.format(e))
            print('there was error when connecting DB, error msg is: {}.'.format(e))
            raise e
        except Exception as e:
            #             print('connect exception: {}'.format(e))
            print('connect exception: {}'.format(e))
            raise e
        else:
            return engine, session, meta

    def query_with_string(self, sql_string, to_list=True, stop_on_error=False, collect_report=True):
        """execute query sql string to db

        Arguments:
            sql_string {str} -- sql cmd
            to_list {bool} -- if to list

        Returns:
            result -- execute sql db results
        """
        try:
            engine, session, meta = self._connect()
            results = session.execute(sql_string)
            if to_list:
                return [dict(u) for u in results]
            else:
                return results
        except Exception as e:
            #             print("[Error] Failed to excute query sql from db, error message: {}".format(e))
            print("[Error] Failed to excute query sql from db, error message: {}".format(e))
            raise e
        finally:
            if session:
                session.close()

    def modify_with_string(self, sql_string, stop_on_error=False, collect_report=True):
        """execute update delete add sql string to db

        Arguments:
            sql_string {str} -- sql cmd

        """
        try:
            engine, session, meta = self._connect()
            session.execute(sql_string)
            session.commit()
        except Exception as e:
            #             print("[Error] Failed to excute modify sql from db, error message: {}".format(e))
            print("[Error] Failed to excute modify sql from db, error message: {}".format(e))
            raise e
        finally:
            if session:
                session.close()

    def count_with_string(self, sql_string, stop_on_error=False, collect_report=True):
        """count select result

        Arguments:
            sql_string {str} -- sql cmd

        """
        try:
            result = self.query_with_string(sql_string)
        except Exception as e:
            #             print("[Error] Failed to excute query sql from db when count, error message: {}".format(e))
            print("[Error] Failed to excute query sql from db when count, error message: {}".format(e))
            raise e
        else:
            if result:
                return len(result)


Base = declarative_base()

if __name__ == '__main__':
    server = "192.168.100.5"
    username = "IEC4SNC"
    password = "IEC4SNC"

    db = Oracle(username, password, server, 1521, 'xe')

    import pprint

    pp = pprint.PrettyPrinter(indent=4)
    sql_string = "SELECT * FROM \"contract\""
    result = db.query_with_string(sql_string)

    pp.pprint(result)


