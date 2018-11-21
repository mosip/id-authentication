## Stage:1 ##
FROM node:10 as builder
RUN mkdir /usr/src/app
WORKDIR /usr/src/app
ENV PATH /usr/src/app/node_modules/.bin:$PATH
COPY package.json /usr/src/app/package.json
RUN npm install
RUN npm install -g @angular/cli@6.1.4 --unsafe
COPY . /usr/src/app
RUN ng build --prod

## Stage:2 ##
FROM nginx:1.13.9-alpine
COPY --from=builder /usr/src/app/dist/pre-registration /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
