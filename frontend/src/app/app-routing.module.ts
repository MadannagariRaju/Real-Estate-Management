import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './homeFolder/home-page/home-page.component';
import { RoleSelectionComponent } from './role-selection/role-selection.component';
import { AgentComponent } from './agentFolder/agent/agent.component';
import { CustomerComponent } from './customerFolder/customer/customer.component';
import { AddPropertyComponent } from './ownerFolder/add-property/add-property.component';
import { authGuard } from './auth/auth.guard';  // Correctly import the auth guard
import { ListPropertyComponent } from './ownerFolder/list-property/list-property.component';
import { OwnerComponent } from './ownerFolder/owner/owner.component';
import { CreateProfileComponent } from './ownerFolder/create-profile/create-profile.component';
import { MessageCustomerComponent } from './ownerFolder/message-customer/message-customer.component';
import { BussinessProfileComponent } from './agentFolder/bussiness-profile/bussiness-profile.component';
import { FindAgentComponent } from './ownerFolder/find-agent/find-agent.component';
import { MessageOwnerComponent } from './agentFolder/message-owner/message-owner.component';
import { ShowReviewsComponent } from './agentFolder/show-reviews/show-reviews.component';
import { ShowPropertiesComponent } from './homeFolder/show-properties/show-properties.component';
import { ContactedComponent } from './customerFolder/contacted/contacted.component';
import { MortgageCalculatorComponent } from './customerFolder/mortgage-calculator/mortgage-calculator.component';
import { ContactedAgentsComponent } from './ownerFolder/contacted-agents/contacted-agents.component';

const routes: Routes = [
  { path: '', component: HomePageComponent },
  {path: 'show-properties', component:ShowPropertiesComponent,data: {role:'unknown'}},
  { path: 'select-role', component: RoleSelectionComponent, },
  { path: 'owner-dashboard', component: OwnerComponent,canActivate: [authGuard],  data: { role: 'OWNER' }  },
  { path: 'agent-dashboard', component: AgentComponent, canActivate: [authGuard], data: { role: 'AGENT' } },
  { path: 'customer-dashboard', component: CustomerComponent, canActivate: [authGuard], data: { role: 'CUSTOMER' } },
  { path: 'add-property', component: AddPropertyComponent, canActivate: [authGuard], data: { role: ['OWNER','AGENT'] } },
  { path: 'list-property', component: ListPropertyComponent, canActivate: [authGuard], data: { role: ['OWNER','AGENT'] } },
  { path: 'create-profile', component: CreateProfileComponent, canActivate: [authGuard], data: { role: ['OWNER', 'CUSTOMER'] }  },
  { path: 'message-customer', component: MessageCustomerComponent, canActivate: [authGuard], data: { role: ['OWNER','AGENT'] } },
  { path: 'bussiness-profile', component: BussinessProfileComponent, canActivate: [authGuard], data: { role: 'AGENT' } },
  {path: 'find-agent', component:FindAgentComponent, canActivate: [authGuard], data:{role:'OWNER'}},
  {path:'message-owner', component:MessageOwnerComponent, canActivate: [authGuard], data:{role:'AGENT'}},
  {path: 'show-reviews', component:ShowReviewsComponent, canActivate: [authGuard], data:{role:'AGENT'}} ,
  {path: 'contacted', component:ContactedComponent, canActivate: [authGuard], data:{role:'CUSTOMER'}}  ,
  {path: 'mortgage-calculator', component:MortgageCalculatorComponent, canActivate:[authGuard], data:{role:'CUSTOMER'}},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}





