import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { CommentModel } from '../model/CommentModel';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http: HttpClient) { }

  public findAllByBookId(bookId: string) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book/"+bookId+"/comment";
    let commentRequest  = this.http.get<CommentModel[]>(url, httpOptions);
    return commentRequest;
  }

  public deleteCommentById(bookId:string, commentId:string) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams,
      responseType: 'text' as 'text'
    };
    let url: string = "/api/v1/book/" + bookId + "/comment/" + commentId;
    let commentRequest  = this.http.delete(url, httpOptions);
    return commentRequest;
  }

  public insert(bookId:string, comment:CommentModel) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book/" + bookId + "/comment" ;
    let commentRequest  = this.http.post<CommentModel>(url, comment, httpOptions);
    return commentRequest;
  }

  public update(bookId:string, comment:CommentModel) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book/" + bookId + "/comment";
    let commentRequest  = this.http.put<CommentModel>(url, comment, httpOptions);
    return commentRequest;
  }

}
