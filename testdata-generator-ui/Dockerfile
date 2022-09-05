FROM node:14-alpine

ARG API_URL="http://localhost:9001/api"
ENV NUXT_HOST="0.0.0.0"
ENV NUXT_PORT=5000


# create destination directory
RUN mkdir -p /usr/src/nuxt-app
WORKDIR /usr/src/nuxt-app

# update and install dependency
RUN apk update && apk upgrade
RUN apk add git

# copy the app, note .dockerignore
COPY . /usr/src/nuxt-app/
RUN npm install
RUN npm run build

EXPOSE 5000


CMD [ "npm", "start" ]
