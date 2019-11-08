# -*- coding: utf-8 -*-

from __future__ import unicode_literals

import time

import pytest


def time_function(fn, *args, **kwargs):
    t1 = time.time()
    fn(*args, **kwargs)
    t2 = time.time()
    return t2 - t1


def test_json_response(api, endpoints):
    assert api.get('user/jimmy').body == endpoints['/user/jimmy']['body']
    assert api.get('user/имя').body == endpoints['/user/имя']['body']
    assert api.get('has_self').body == endpoints['/has_self']['body']


def test_non_json_response(api):
    with pytest.raises(ValueError):
        api.get('nojson')
    assert api.get('nojson', silent=True) is None


def test_request_delay(api):
    api.config['delay'] = 0.2
    assert time_function(api.get, 'test') >= 0.2
    assert time_function(api.get, 'test', delay=0.1) >= 0.1
    assert time_function(api.get, 'test') >= 0.2


def test_request_methods(api):
    assert api.put('put_endpoint').body['message'] == "Success!"
    assert api.post('post_endpoint').body['message'] == "Success!"
    assert api.patch('patch_endpoint').body['message'] == "Success!"
    assert api.delete('delete_endpoint').body['message'] == "Success!"
    assert not api.head('head_endpoint').body


def test_config_endpoint(api, endpoints):
    assert api.get('config') == endpoints['/config']['body']
