import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FileUploadComponent} from './file-upload/file-upload.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {MessageDetailComponent} from './message-detail/message-detail.component';
import {MessageValidationComponent} from './message-validation/message-validation.component';
import {SettingsComponent} from './settings/settings.component';
import { DetectionDocComponent } from './documentation/detection-doc/detection-doc.component';


const routes: Routes = [
  { path: 'file', component: FileUploadComponent }
  ,  { path: 'dashboard', component: DashboardComponent }
  ,  { path: 'detections', component: DetectionDocComponent }
  ,  { path: 'messages', component: MessageDetailComponent }
  ,  { path: 'validation', component: MessageValidationComponent }
  ,  { path: 'settings', component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
