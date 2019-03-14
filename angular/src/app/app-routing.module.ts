import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FileUploadComponent} from './file-upload/file-upload.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {MessageDetailComponent} from './message-detail/message-detail.component';
import {SettingsComponent} from './settings/settings.component';


const routes: Routes = [
  { path: 'file', component: FileUploadComponent }
  ,  { path: 'dashboard', component: DashboardComponent }
  ,  { path: 'messages', component: MessageDetailComponent }
  ,  { path: 'settings', component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
