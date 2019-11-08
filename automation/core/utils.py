import urllib.parse
from automation.core import logger, exceptions
from formats import FormatBank, discover_json, discover_yaml


formats = FormatBank()

discover_json(formats, content_type='application/json')
discover_yaml(formats, content_type='application/x-yaml')

def query_json(json_content, query, delimiter='.'):
    """ Do an xpath-like query with json_content.

    Args:
        json_content (dict/list/string): content to be queried.
        query (str): query string.
        delimiter (str): delimiter symbol.

    Returns:
        str: queried result.

    Examples:
        >>> json_content = {
            "ids": [1, 2, 3, 4],
            "person": {
                "name": {
                    "first_name": "Leo",
                    "last_name": "Lee",
                },
                "age": 29,
                "cities": ["Guangzhou", "Shenzhen"]
            }
        }
        >>>
        >>> query_json(json_content, "person.name.first_name")
        >>> Leo
        >>>
        >>> query_json(json_content, "person.name.first_name.0")
        >>> L
        >>>
        >>> query_json(json_content, "person.cities.0")
        >>> Guangzhou

    """
    raise_flag = False
    response_body = u"response body: {}\n".format(json_content)
    try:
        for key in query.split(delimiter):
            if isinstance(json_content, (list, str)):
                json_content = json_content[int(key)]
            elif isinstance(json_content, dict):
                json_content = json_content[key]
            else:
                logger.log_error(
                    "invalid type value: {}({})".format(json_content, type(json_content)))
                raise_flag = True
    except (KeyError, ValueError, IndexError):
        raise_flag = True

    if raise_flag:
        err_msg = u"Failed to extract! => {}\n".format(query)
        err_msg += response_body
        logger.log_error(err_msg)
        raise exceptions.ExtractFailure(err_msg)

    return json_content



def url_path_join(*parts):
    """Normalize url parts and join them with a slash."""
    schemes, netlocs, paths, queries, fragments = zip(*(urllib.parse.urlsplit(part) for part in parts))
    scheme = first(schemes)
    netloc = first(netlocs)
    path = '/'.join(x.strip('/') for x in paths if x)
    query = first(queries)
    fragment = first(fragments)
    return urllib.parse.urlunsplit((scheme, netloc, path, query, fragment))

def first(sequence, default=''):
    return next((x for x in sequence if x), default)


if __name__ == '__main__':
    json_content = {
        "ids": [1, 2, 3, 4],
        "person": {
            "name": {
                "first_name": "Leo",
                "last_name": "Lee",
            },
            "age": 29,
            "cities": ["Guangzhou", "Shenzhen"]
        }
    }
    print(query_json(json_content, "person.name.first_name"))
