import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AngularFireStorage } from '@angular/fire/storage';
declare var ol: any;
declare var mylocation : any
@Component({
  selector: 'app-userwindow',
  templateUrl: './userwindow.component.html',
  styleUrls: ['./userwindow.component.css']
})
export class UserwindowComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private afStorage: AngularFireStorage) { }
  viewdata : any
  map:any
  gmapurl: any
  mylocation : any
  ngOnInit() {
    this.activatedRoute.data.subscribe(data => {
      this.viewdata = data.data;
      const task = this.afStorage.ref('pictures/' + this.viewdata.Image).getDownloadURL()
        return task.subscribe(url => {
          if (url) {
            this.viewdata.address = url;
          }
          this.gmapurl = `https://www.google.com/maps/search/?api=1&query=${this.viewdata.Location.F},${this.viewdata.Location.V}`
          this.setCenter(this.viewdata.Location.V,this.viewdata.Location.F);
          this.add_map_point(this.viewdata.Location.V,this.viewdata.Location.F);
        })
    });
    this.map = new ol.Map({
      target: 'map',
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM()
        })
      ],
      view: new ol.View({
        center: ol.proj.fromLonLat([this.viewdata.Location.V,this.viewdata.Location.F]),
        zoom: 8
      })
    });
    if (!navigator.geolocation) {
      console.log('Geolocation is not supported by your browser');
    } else {
      navigator.geolocation.getCurrentPosition((position)=>{
        this.mylocation = position
      } )
    } 

  }
  setCenter(lat,lng) {
    var view = this.map.getView();
    view.setCenter(ol.proj.fromLonLat([lat,lng]));
    view.setZoom(10);
  }

  add_map_point(lat, lng) {
    var marker = new ol.Feature({
      geometry: new ol.geom.Point(
        ol.proj.fromLonLat([lat,lng])
      ),
    });
    marker.setStyle(new ol.style.Style({
      image: new ol.style.Icon(({
          crossOrigin: 'anonymous',
          src: '../../assets/images/map-pin.svg'
      }))
  }));
    var vectorSource = new ol.source.Vector({
      features: [marker]
    });
    var markerVectorLayer = new ol.layer.Vector({
      source: vectorSource,
    });
    this.map.addLayer(markerVectorLayer);

  };
    pointmyLocation(){
      this.setCenter(parseFloat(this.mylocation.coords.longitude),parseFloat(this.mylocation.coords.latitude))
      this.add_map_point(parseFloat(this.mylocation.coords.longitude),parseFloat(this.mylocation.coords.latitude))
  }
  backtosamelocation(){
    this.setCenter(this.viewdata.Location.V,this.viewdata.Location.F)
  }
 
}
