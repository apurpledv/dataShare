import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeService {
  constructor() { }

  public getDaysBeforeExpiring(dateSent: Date): number {
      let currentDate = new Date();
      dateSent = new Date(dateSent);

      return Math.floor((
        Date.UTC(dateSent.getFullYear(), dateSent.getMonth(), dateSent.getDate())
        - Date.UTC(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate())
        ) / (1000 * 60 * 60 * 24)
      );
  }
}
