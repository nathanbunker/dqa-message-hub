import { AppRoutingModule } from './app-routing.module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { TableModule } from 'primeng/table';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
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
  faPause, faDownload, faChevronRight, faChevronDown, faSearch, faChevronLeft, faFilter, faSpinner, faCog, faTimes, faThumbsDown, faThumbsUp, faEnvelope, faSyringe, faExclamationTriangle, faEye, faEyeSlash
} from '@fortawesome/free-solid-svg-icons';
import { FileUploadDetailsComponent } from './file-upload/file-upload-details/file-upload-details.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CalendarComponent } from './dashboard/calendar/calendar.component';
import { MessageDetailComponent } from './message-detail/message-detail.component';
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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DatepickerComponent } from './dashboard/datepicker/datepicker.component';
import { DocumentationService } from './services/documentation.service';
import { ReportingService } from './services/reporting.service';
import { ProviderComponent } from './dashboard/provider/provider.component';
import { ProviderDashboardGuard } from './guards/provider-dashboard.guard';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Hl7Reference } from './hl7-reference';
import { Hl7ViewComponent } from './hl7-view/hl7-view.component';
import { ReportComponent } from './report/report.component';
import { DurationPipe } from './pipes/duration.pipe';
import { LoginComponent } from './login/login.component';
import { AuthenticatedGuard, NotAuthenticatedGuard } from './services/auth-guard.service';
import { AuthInterceptor } from './services/auth-interceptor.service';

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
    DatepickerComponent,
    ProviderComponent,
    Hl7ViewComponent,
    ReportComponent,
    DurationPipe,
    LoginComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FontAwesomeModule,
    HttpClientModule,
    Ng2GoogleChartsModule,
    NgbModule,
    TableModule,
    FormsModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    ToastrModule.forRoot({ positionClass: 'toast-top-right' }),
  ],
  providers: [
    DocumentationService,
    ReportingService,
    ProviderDashboardGuard,
    Hl7Reference,
    AuthenticatedGuard,
    NotAuthenticatedGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
  exports: [ReportComponent]
})
export class AppModule {
  constructor() {
    // Add an icon to the library for convenient access in other components
    library.add(
      faSlidersH,
      faCoffee,
      faStethoscope,
      faCaretDown,
      faTrashAlt,
      faDownload,
      faChevronRight,
      faChevronLeft,
      faThumbsDown,
      faThumbsUp,
      faChevronDown,
      faSearch,
      faFilter,
      faSpinner,
      faEnvelope,
      faSyringe,
      faExclamationTriangle,
      faCog,
      faTimes,
      faEye,
      faEyeSlash,
    );
  }
}
