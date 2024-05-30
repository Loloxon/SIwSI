import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MatDivider} from "@angular/material/divider";
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from "@angular/material/stepper";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {CaptchaSelectComponent} from "./captcha-select/captcha-select.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatDivider, MatStep, MatStepper, ReactiveFormsModule, MatButton, MatStepLabel, MatStepperNext, MatStepperPrevious, CaptchaSelectComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'kogni';
}
