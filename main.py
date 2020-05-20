# pip install --upgrade google-api-python-client google-auth-httplib2 google-auth-oauthlib
from __future__ import print_function

import base64
import http
import io
import pickle
import os.path
import socketserver
import webbrowser
from http.server import BaseHTTPRequestHandler
import urllib.parse
from uuid import uuid4

from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request


def code_verifier_gen(n_bytes=64):
    """
    Generates a 'code_verifier' as described in section 4.1 of RFC 7636.
    This is a 'high-entropy cryptographic random string' that will be
    impractical for an attacker to guess.
    Args:
        n_bytes: integer between 31 and 96, inclusive. default: 64
            number of bytes of entropy to include in verifier.
    Returns:
        Bytestring, representing urlsafe base64-encoded random data.
    """
    verifier = base64.urlsafe_b64encode(os.urandom(n_bytes)).rstrip(b'=')
    # https://tools.ietf.org/html/rfc7636#section-4.1
    # minimum length of 43 characters and a maximum length of 128 characters.
    if len(verifier) < 43:
        raise ValueError("Verifier too short. n_bytes must be > 30.")
    elif len(verifier) > 128:
        raise ValueError("Verifier too long. n_bytes must be < 97.")
    else:
        return verifier


def main():

    class Handler(BaseHTTPRequestHandler):
        def _set_response(self):
            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()

        def do_GET(self):
            query = "http://localhost" + self.path
            parsed = urllib.parse.parse_qs(urllib.parse.urlparse(query).query)
            if ("error" in parsed or ("code" not in parsed or "state" not in parsed)):
                print("ERROR")
                return
            code = parsed["code"][0]
            state = parsed["state"][0]

            print("AUTH CODE: ", code)
            print("incoming state: ", state)

            self._set_response()
            self.wfile.write("auth code: {}\nincoming state: {}".format(code, state).encode('utf-8'))

    with socketserver.TCPServer(("", 0), Handler) as httpd:
        PORT = httpd.server_address[1]
        print("serving at port", PORT)

        redirectURI = "http://localhost:" + str(PORT)  # unused port 25563

        authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth"
        tokenEndpoint = "https://www.googleapis.com/oauth2/v4/token"
        userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo"

        state = uuid4()
        code_verifier = code_verifier_gen()
        code_challenge = code_verifier
        code_challenge_method = "plain"

        authorizationRequest = "{0}?response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive.metadata.readonly&redirect_uri={1}&client_id={2}&state={3}&code_challenge={4}&code_challenge_method={5}".format(
            authorizationEndpoint,
            redirectURI,
            "788835257396-kgu68qak4ku4f2tsap06q8dcire73sph.apps.googleusercontent.com",  # Client id
            state,
            code_challenge,
            code_challenge_method
        )

        webbrowser.open(authorizationRequest)

        httpd.handle_request()


if __name__ == '__main__':
    main()
