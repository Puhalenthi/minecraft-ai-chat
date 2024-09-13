import os
import urllib
import flask
import time
from openai import OpenAI

token = os.environ["GITHUB_TOKEN"]
endpoint = "https://models.inference.ai.azure.com"
model_name = "gpt-4o"

client = OpenAI(
    base_url=endpoint,
    api_key=token,
)

def getCommand(username, prompt):
    response = client.chat.completions.create(
        messages = [
                {"role" : "system",
                "content" : "You are a Minecraft command line chatbot in MC version 1.20.1. Your role is to read a request from the user and output a valid Minecraft command that accomplishes it. ONLY output the command (without markdown): " + prompt + ". Player's username that is requesting this is: " + username}
            ],
        model=model_name,
        temperature=1.0,
        max_tokens=1000,
        top_p=1.0
    )

    #print('Completion Tokens - ' + str(response["usage"]["completion_tokens"]))
    # print('Prompt Tokens - ' + str(response["usage"]["prompt_tokens"]))
    # print('Total Tokens - ' + str(response["usage"]["total_tokens"]))
    # return response["choices"][0]["message"]["content"]
    print(response.choices[0].message.content)


def main(prompt, username):
    start = time.time()
    command = getCommand(prompt, username)
    end = time.time()
    print(str(end - start) + " seconds to get command.")
    return command

flaskApp = flask.Flask(__name__)

@flaskApp.route("/mcchatbot", methods=["GET"])
def mcchatbot():
    prompt = flask.request.args.get("prompt")
    prompt = urllib.parse.unquote_plus(prompt)
    username = flask.request.args.get("username")
    username = urllib.parse.unquote_plus(username)
    command = main(username, prompt)
    print(command)
    print('\n---------------------------------------------------------------------------------\n')
    return command

if __name__ == "__main__":
    flaskApp.run()