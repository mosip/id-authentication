import logging
import os
import requests
import logging

class RestUtility:

    def __init__(self, authorization_header_constant):
        self.request_headers = {
                                'Authorization': authorization_header_constant,
                                'content-type': 'application/json',
                               }
        self.logger = logging.getLogger(__name__)
    
    def get_request(self, server_url=None, path_params=None, headers={}, data=None, cookies=None):
        
        if path_params:
            server_url += path_params
        print('URL and Path Params: {}'.format(server_url))
        return requests.get(
            server_url,
            headers = headers,
            data = data,
            cookies = cookies,
        )
    
    def post_request(self, server_url=None, path_params=None, additional_headers={}, data=None, cookies=None):
        # is it required to add validation for data is not None?
        
        if path_params:
            if not server_url.endswith('/'):
                server_url += '/'
            server_url += path_params

        if additional_headers:
            self.request_headers.update(additional_headers)

        print('Request for URL: {}'.format(server_url))
        return requests.post(
            server_url,
            headers = self.request_headers,
            data = data,
            cookies = cookies,
        )
