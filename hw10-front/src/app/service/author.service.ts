import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { AuthorModel } from '../model/AuthorModel';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {

  constructor(private http: HttpClient) { }

  public findAllAuthor() {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/author";
    let authorRequest  = this.http.get<AuthorModel[]>(url, httpOptions);
    return authorRequest;
  }


  public deleteAuthorById(authorId: string) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams,
      responseType: 'text' as 'text'
    };
    let url: string = "/api/v1/author/" + authorId;
    let authorRequest  = this.http.delete(url, httpOptions);
    return authorRequest;
  }
}
