
TYPES = ['Basic Auth', 'No Auth']

class NoAuth:

    def info(self, data=None):
        return None

class BasicAuth:

    def info(self, data):
        return {'auth': (data['username'], data['password'])}

def get_auth(type='No Auth'):
    info = {
        "No Auth": NoAuth,
        "Basic Auth": BasicAuth
    }
    if type not in TYPES:
        type = 'No Auth'
    return info[type]()