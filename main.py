# pip install --upgrade google-api-python-client google-auth-httplib2 google-auth-oauthlib
from __future__ import print_function

import base64
import http
import io
import pickle
import os.path
import socketserver
import webbrowser
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
    PORT = 25563

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
        "788835257396-kgu68qak4ku4f2tsap06q8dcire73sph.apps.googleusercontent.com", # Client id
        state,
        code_challenge,
        code_challenge_method
    )

    webbrowser.open(authorizationRequest)

    Handler = http.server.SimpleHTTPRequestHandler

    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        print("serving at port", PORT)
        httpd.serve_forever()
    return
    
    """Shows basic usage of the Drive v3 API.
    Prints the names and ids of the first 10 files the user has access to.
    """
    creds = None
    # The file token.pickle stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    # If there are no (valid) credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)

        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    service = build('drive', 'v3', credentials=creds)

    # Call the Drive v3 API
    results = service.files().list(
        pageSize=10, fields="nextPageToken, files(id, name)").execute()
    items = results.get('files', [])

    if not items:
        print('No files found.')
    else:
        print('Files:')
        for item in items:
            print(u'{0} ({1})'.format(item['name'], item['id']))


if __name__ == '__main__':
    main()
