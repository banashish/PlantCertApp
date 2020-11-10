import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { ConnectorService } from './connector.service';

@Injectable({
  providedIn: 'root'
})
export class ResolvecerificateService implements Resolve<any> {

  constructor(private connect : ConnectorService) { }
  resolve() {
    return this.connect.getInfo()
  }
}
