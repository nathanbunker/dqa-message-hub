import { Component, OnInit } from '@angular/core';
import { SettingsService, Settings } from '../settings.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  settings: Observable<Settings>;

  messageEvaluation: {
    authid: string,
    api: string,
    activation: string
  };

  constructor(private settingsService: SettingsService) { }

  submit(settings: Settings) {
    this.settingsService.saveSettings(settings);
  }

  ngOnInit() {
    this.settings = this.settingsService.getSettings();
  }

}
