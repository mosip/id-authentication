FROM anapsix/alpine-java:8_jdk_unlimited

# Required to be passed at docker build time like: docker build --build-arg softhsm_pin=1234 --tag kernel-keymanager-service:1.0 .
ARG softhsm_pin

ENV softhsm_pin_env=${softhsm_pin}

RUN apk --update add \
        alpine-sdk \
        autoconf \
        automake \
        expect \
        git \
        libtool \
        openssl-dev

RUN git clone https://github.com/opendnssec/SoftHSMv2.git /tmp/softhsm2

WORKDIR /tmp/softhsm2

RUN git checkout 2.5.0 -b 2.5.0 \
    && sh autogen.sh \
    && ./configure --prefix=/softhsm \ 
    && make \
    && make install

WORKDIR /root

RUN rm -rf /tmp/softhsm2

RUN chmod -R 777 /softhsm

RUN /softhsm/bin/softhsm2-util --init-token --slot 0 --pin ${softhsm_pin_env} --so-pin ${softhsm_pin_env} --label 'Keymanager_token'