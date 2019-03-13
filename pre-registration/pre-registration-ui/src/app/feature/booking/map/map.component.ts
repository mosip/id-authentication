import { Component, OnInit, Input } from '@angular/core';
import OlMap from 'ol/Map';
import OlVectorSource from 'ol/source/Vector';
import OlVectorLayer from 'ol/layer/Vector';
import OlView from 'ol/View';
import OlFeature from 'ol/Feature';
import OlPoint from 'ol/geom/Point';
import OlXyzSource from 'ol/source/XYZ';
import OlTileLayer from 'ol/layer/Tile';
import { Icon, Style } from 'ol/style';

import { fromLonLat, addCommon as addCommonProjections } from 'ol/proj';
import { SharedService } from '../booking.service';

@Component({
    selector: 'app-map',
    templateUrl: './map.component.html',
    styleUrls: ['./map.component.css']
})

export class MapComponent implements OnInit {

    @Input() mapProvider: string;
    lonLat: any;
    map: OlMap;
    vectorSource: OlVectorSource;
    vectorLayer: OlVectorLayer;
    xyzSource: OlXyzSource;
    tileLayer: OlTileLayer;
    view: OlView;
    marker: OlFeature;
    markerStyle: Style;
    url: string;
    centers: any;
    markers = [];

    // tslint:disable-next-line:max-line-length
    googleMapsUrl = 'http://maps.google.com/maps/vt?pb=!1m5!1m4!1i{z}!2i{x}!3i{y}!4i256!2m3!1e0!2sm!3i375060738!3m9!2spl!3sUS!5e18!12m1!1e47!12m3!1e37!2m1!1ssmartmaps!4e0';

    OSM_URL = 'http://tile.osm.org/{z}/{x}/{y}.png';

    constructor(private service: SharedService) { }

    ngOnInit() {

        if (this.mapProvider === 'OSM') {
            this.url = this.OSM_URL;
        } else if (this.mapProvider === 'GMAPS') {
            this.url = this.googleMapsUrl;
        }

        this.service.coordinatesList.subscribe(list => {
            this.centers = list;
        });

        this.service.currentCoordinates.subscribe(coordinates => {
            this.lonLat = coordinates;
            if (!this.map) {
                this.createMap();
            } else {
                this.changeCenter();
            }
        });

    }

    createMap() {
        console.log(this.lonLat);
        console.log(this.centers);

        addCommonProjections();

        for (let i in this.centers) {
            this.marker = new OlFeature({
                geometry: new OlPoint(fromLonLat([this.centers[i]['longitude'], this.centers[i]['latitude']]))
            });
            this.markers.push(this.marker);
        }

        this.vectorSource = new OlVectorSource({
            features: this.markers
        });

        this.markerStyle = new Style({
            image: new Icon(({
                anchor: [0.5, 46],
                anchorXUnits: 'fraction',
                anchorYUnits: 'pixels',
                opacity: 0.75,
                src: 'assets/marker.png'
            }))
        });

        this.vectorLayer = new OlVectorLayer({
            source: this.vectorSource,
            style: this.markerStyle
        });

        /* XYZ */

        this.xyzSource = new OlXyzSource({
            url: this.url
        });

        this.tileLayer = new OlTileLayer({
            source: this.xyzSource
        });

        /* View and map */

        this.view = new OlView({
            center: fromLonLat(this.lonLat),
            zoom: 14
        });

        this.map = new OlMap({
            target: 'map',
            // Added both layers
            layers: [this.tileLayer, this.vectorLayer],
            view: this.view
        });
    }

    changeCenter() {
        console.log(this.lonLat);
        const view = this.map.getView();
        view.setCenter(fromLonLat([this.lonLat[0], this.lonLat[1]]));
        view.setZoom(14);
    }
}
