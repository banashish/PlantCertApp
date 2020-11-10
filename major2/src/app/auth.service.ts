import { Injectable } from '@angular/core';
import { Router } from  "@angular/router";
import { auth } from  'firebase/app';
import { AngularFireAuth } from  "@angular/fire/auth";
import { User } from  'firebase';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  user:  User;
  username : string
  constructor(public  afAuth:  AngularFireAuth, public  router:  Router) { }

  login(email: string, password: string) {
    this.username = email;
    return this.afAuth.auth.signInWithEmailAndPassword(email, password)    
  }

  getData(){
    return this.afAuth.authState
     
  }
  logout(){
    localStorage.removeItem('uid')
    this.afAuth.auth.signOut();
  }



}
