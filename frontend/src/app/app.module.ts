import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './homeFolder/home-page/home-page.component';
import { RoleSelectionComponent } from './role-selection/role-selection.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AgentComponent } from './agentFolder/agent/agent.component';
import { CustomerComponent } from './customerFolder/customer/customer.component';
import { AddPropertyComponent } from './ownerFolder/add-property/add-property.component';
import { ListPropertyComponent } from './ownerFolder/list-property/list-property.component';
import { OwnerComponent } from './ownerFolder/owner/owner.component';
import { CreateProfileComponent } from './ownerFolder/create-profile/create-profile.component';
import { MessageCustomerComponent } from './ownerFolder/message-customer/message-customer.component';
import { OwnerNavbarComponent } from './ownerFolder/owner-navbar/owner-navbar.component';
import { CommonModule } from '@angular/common';
import { BussinessProfileComponent } from './agentFolder/bussiness-profile/bussiness-profile.component';
import { AgentNavbarComponent } from './agentFolder/agent-navbar/agent-navbar.component';
import { FindAgentComponent } from './ownerFolder/find-agent/find-agent.component';
import { MessageOwnerComponent } from './agentFolder/message-owner/message-owner.component';
import { ShowReviewsComponent } from './agentFolder/show-reviews/show-reviews.component';
import { ShowPropertiesComponent } from './homeFolder/show-properties/show-properties.component';
import { GoogleMapsModule } from '@angular/google-maps';
import { ToastrModule } from 'ngx-toastr';
import { ContactedComponent } from './customerFolder/contacted/contacted.component';
import { MortgageCalculatorComponent } from './customerFolder/mortgage-calculator/mortgage-calculator.component';
import { NavbarComponent } from './customerFolder/navbar/navbar.component';
import { IndianCurrencyFormatPipe } from './indian-currency-format.pipe';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    RoleSelectionComponent,
    OwnerComponent,
    AgentComponent,
    CustomerComponent,
    AddPropertyComponent,
    ListPropertyComponent,
    CreateProfileComponent,
    MessageCustomerComponent,
    OwnerNavbarComponent,
    BussinessProfileComponent,
    AgentNavbarComponent,
    FindAgentComponent,
    MessageOwnerComponent,
    ShowReviewsComponent,
    ShowPropertiesComponent,
    ContactedComponent,
    MortgageCalculatorComponent,
    NavbarComponent,
    IndianCurrencyFormatPipe,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule,ReactiveFormsModule ,CommonModule,GoogleMapsModule,ToastrModule.forRoot({
    positionClass: 'toast-top-right', // Center the toastr
    timeOut: 3000, // Set time out for how long the toaster stays
    progressBar: true,
    closeButton: true,

  }),  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}

