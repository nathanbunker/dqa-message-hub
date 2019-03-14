import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {HttpClientModule} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { library } from '@fortawesome/fontawesome-svg-core';
import {
  faCoffee,
  faSlidersH,
  faStethoscope,
  faCaretDown,
  faTrashAlt,
  faPause, faDownload
} from '@fortawesome/free-solid-svg-icons';
import { FileUploadDetailsComponent } from './file-upload/file-upload-details/file-upload-details.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CalendarComponent } from './dashboard/calendar/calendar.component';
import { MessageDetailComponent } from './message-detail/message-detail.component';
import { ReportComponent } from './dashboard/report/report.component';
import { MessagesComponent } from './dashboard/report/messages/messages.component';
import { DetectionsComponent } from './dashboard/report/detections/detections.component';
import { CodesComponent } from './dashboard/report/codes/codes.component';
import { VaccinesComponent } from './dashboard/report/vaccines/vaccines.component';
import { ScoreComponent } from './dashboard/report/score/score.component';
import { MessageValidationComponent } from './message-validation/message-validation.component';
import { DocumentationComponent } from './documentation/documentation.component';
import { SettingsComponent } from './settings/settings.component';
import { CodeDocComponent } from './documentation/code-doc/code-doc.component';
import { DetectionDocComponent } from './documentation/detection-doc/detection-doc.component';
import { Ng2GoogleChartsModule } from 'ng2-google-charts';
import { ProviderTypeaheadComponent } from './dashboard/provider-typeahead/provider-typeahead.component';
import { FormsModule } from '@angular/forms';
import { DatepickerComponent } from './dashboard/datepicker/datepicker.component';

@NgModule({
  declarations: [
    AppComponent,
    FileUploadComponent,
    HeaderComponent,
    FileUploadDetailsComponent,
    FooterComponent,
    DashboardComponent,
    CalendarComponent,
    MessageDetailComponent,
    ReportComponent,
    MessagesComponent,
    DetectionsComponent,
    CodesComponent,
    VaccinesComponent,
    ScoreComponent,
    MessageValidationComponent,
    DocumentationComponent,
    SettingsComponent,
    CodeDocComponent,
    DetectionDocComponent,
    ProviderTypeaheadComponent,
    DatepickerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FontAwesomeModule,
    HttpClientModule,
    Ng2GoogleChartsModule,
    NgbModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
  // Add an icon to the library for convenient access in other components
  library.add(faSlidersH, faCoffee, faStethoscope, faCaretDown, faTrashAlt, faDownload);
  }
}
