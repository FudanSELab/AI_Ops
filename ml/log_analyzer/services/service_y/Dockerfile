FROM erkekin/tornado

RUN mkdir -p /app
WORKDIR /app

ADD ./y_server.py /app/
CMD [ "python", "y_server.py" ]

EXPOSE 16101