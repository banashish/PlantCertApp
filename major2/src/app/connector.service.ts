import { Injectable } from '@angular/core';
import { AngularFirestore } from '@angular/fire/firestore';
import { Policy } from 'src/app/policy.model';
import { switchMap, mergeMap, map } from 'rxjs/operators';
import { pipe } from 'rxjs';
import { AngularFireStorage } from '@angular/fire/storage';
import { element } from 'protractor';

@Injectable({
  providedIn: 'root'
})
export class ConnectorService {

  constructor(private firestore: AngularFirestore, private afStorage: AngularFireStorage) { }

  policies: Policy[] = []
  products: any
  personData : any

  getPolicies() {
    return this.firestore.collection('plants')
  }

  createPlant(data,id) {
    return this.firestore.collection('plants').doc(id).set(data);
  }

  updatePolicy(policy: Policy) {
    delete policy.id;
    this.firestore.doc('policies/' + policy.id).update(policy);
  }

  deletePlant(policyId: string) {
    this.firestore.doc('plants/' + policyId).delete();
  }

  setInfo(data){
    this.personData = data;
  }

  getInfo(){
    return this.personData;
  }

}
