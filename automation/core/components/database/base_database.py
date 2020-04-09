from abc import ABC, abstractmethod

class BaseDatabase(ABC):
    """base class of batabase, provide abstract connect, query with string and modify with string methods
    """

    @abstractmethod
    def _connect(self, on_error=True):
        pass


    @abstractmethod
    def query_with_string(self, sql_string, to_list=True, on_error=True):
        pass


    @abstractmethod
    def modify_with_string(self, sql_string, on_error=True):
        pass
