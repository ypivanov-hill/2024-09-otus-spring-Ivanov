import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ButtonModule } from 'primeng/button';

import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { BookListComponent } from './content/book-list/book-list.component';
import { BookEditComponent } from './content/book-edit/book-edit.component';
import { CommentListComponent } from './content/comment-list/comment-list.component';
import { provideHttpClient } from '@angular/common/http';

import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { Select } from 'primeng/select';
import { InputText } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';

import { AutoCompleteModule } from 'primeng/autocomplete';

import { DialogModule } from 'primeng/dialog';
import { AuthorListComponent } from './content/author-list/author-list.component';
import { GenreListComponent } from './content/genre-list/genre-list.component';
import { ButtonGroupModule } from 'primeng/buttongroup';


@NgModule({
  declarations: [
    AppComponent,
    BookListComponent,
    BookEditComponent,
    CommentListComponent,
    AuthorListComponent,
    GenreListComponent

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ButtonModule,
    TableModule,
    CardModule,
    IconFieldModule,
    InputIconModule,
    ReactiveFormsModule,
    ToastModule,
    FormsModule,
    Select,
    InputText,
    MultiSelectModule,
    AutoCompleteModule,
    DialogModule,
    ButtonGroupModule

  ],
  providers: [
    provideHttpClient(),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      }
    })
  ],
  bootstrap: [AppComponent],

})
export class AppModule {
}
