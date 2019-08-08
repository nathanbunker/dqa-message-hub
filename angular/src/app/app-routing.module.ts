import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SettingsComponent } from './settings/settings.component';
import { ProviderComponent } from './dashboard/provider/provider.component';
import { ProviderDashboardGuard } from './guards/provider-dashboard.guard';
import { DetectionDocComponent } from './documentation/detection-doc/detection-doc.component';
import { CodeDocComponent } from './documentation/code-doc/code-doc.component';
import { MessageDetailComponent } from './message-detail/message-detail.component';

const routes: Routes = [
  {
    path: 'file',
    component: FileUploadComponent,
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
  },
  {
    path: 'documentation',
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
    path: 'dashboard/:provider/date/:date',
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
    component: MessageDetailComponent,
  },
  {
    path: 'settings',
    component: SettingsComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
