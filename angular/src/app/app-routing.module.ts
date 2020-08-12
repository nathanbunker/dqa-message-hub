import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { FileModifyComponent } from './file-modify/file-modify.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SettingsComponent } from './settings/settings.component';
import { ProviderComponent } from './dashboard/provider/provider.component';
import { ProviderDashboardGuard } from './guards/provider-dashboard.guard';
import { DetectionDocComponent } from './documentation/detection-doc/detection-doc.component';
import { CodeDocComponent } from './documentation/code-doc/code-doc.component';
import { MessageDetailComponent } from './message-detail/message-detail.component';
import { MessageValidationComponent } from './message-validation/message-validation.component';
import { LoginComponent } from './login/login.component';
import { AuthenticatedGuard, NotAuthenticatedGuard } from './services/auth-guard.service';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [NotAuthenticatedGuard],
  },
  {
    path: 'file',
    component: FileUploadComponent,
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'modify',
    component: FileModifyComponent,
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'validation',
    component: MessageValidationComponent,
    canActivate: [AuthenticatedGuard],
  },
  {
    path: 'documentation',
    canActivate: [AuthenticatedGuard],
    children: [
      {
        path: 'detections',
        component: DetectionDocComponent,
      },
      {
        path: 'codebase',
        component: CodeDocComponent,
      }
    ]
  },
  {
    path: 'dashboard/:provider/date/:dateStart/:dateEnd',
    canActivate: [AuthenticatedGuard],
    children: [
      {
        path: '',
        redirectTo: 'messages',
        pathMatch: 'full',
      },
      {
        path: ':tab',
        component: ProviderComponent,
        runGuardsAndResolvers: 'always',
        canActivate: [ProviderDashboardGuard],
      }
    ],
  },
  {
    path: 'message/:messageId',
    canActivate: [AuthenticatedGuard],
    component: MessageDetailComponent,
  },
  {
    path: 'settings',
    canActivate: [AuthenticatedGuard],
    component: SettingsComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
