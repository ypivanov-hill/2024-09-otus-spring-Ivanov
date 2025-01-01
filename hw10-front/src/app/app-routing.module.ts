import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookListComponent } from './content/book-list/book-list.component';
import { BookEditComponent } from './content/book-edit/book-edit.component';
import { CommentListComponent } from './content/comment-list/comment-list.component';
import { GenreListComponent } from './content/genre-list/genre-list.component';
import { AuthorListComponent } from './content/author-list/author-list.component';

const routes: Routes = [
  {path: 'book-list', component: BookListComponent},
  {path: 'book-edit',  children:[
      {path: ':bookId', component: BookEditComponent},
      {path: ':bookId/comment-list', component: CommentListComponent}
    ]},
  {path: 'genre-list', component: GenreListComponent},
  {path: 'author-list', component: AuthorListComponent},
  {path: '',   redirectTo: '/book-list', pathMatch: 'full'},
  {path: '**', component: BookListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
