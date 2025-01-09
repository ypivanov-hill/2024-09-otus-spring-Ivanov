import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {AuthorModel} from '../model/AuthorModel';
import {GenreModel} from '../model/GenreModel';

@Injectable({
  providedIn: 'root'
})
export class GenreService {

  constructor(private http: HttpClient) { }

  public findAllGenre() {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/genre";
    let booksRequest  = this.http.get<GenreModel[]>(url, httpOptions);
    return booksRequest;
  }
}
