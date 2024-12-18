import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ButtonModule } from 'primeng/button';

import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
BrowserAnimationsModule,
ButtonModule
  ],
  providers: [
provideAnimationsAsync(),
        providePrimeNG({
            theme: {
                preset: Aura
            }
        })
],
  bootstrap: [AppComponent]
})
export class AppModule { }
