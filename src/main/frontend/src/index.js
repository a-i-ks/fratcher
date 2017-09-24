import React from "react";
import {CookiesProvider} from "react-cookie";
import ReactDOM from "react-dom";

import {I18nextProvider} from "react-i18next";
import {HashRouter as Router, Route, Switch} from "react-router-dom";
import Authentication from "./components/authentication";
import Navigation from "./components/navigation"
import Registration from "./components/registration"
import EditProfile from "./components/editProfile"
import MatchingCandidates from "./components/matchingCandidates"

import i18n from "./i18n";
import User from "./util/User";
import MatchList from "./components/matchList";
import MatchDetail from "./components/matchDetail";


// Design decision: We use a global parent component for inter-sibling communication.
class Root extends React.Component {
    constructor(props) {
        super(props);
        // Force initialization of the object.
        User.isAuthenticated();
        this.updateAuthentication = this.updateAuthentication.bind(this);
    }


    // This is called whenever the authentication state of a user is changed by a component. Additionally,
    // this is an example of intersibling communication with a common parent.
    updateAuthentication() {
        //this.nav.updateAuthentication();
        this.forceUpdate();
    }

    render() {
        return (
            <div>
                {User.isAuthenticated() &&
                <Navigation updateAuthentication={this.updateAuthentication} ref={(component) => {
                    this.nav = component;
                }}/>}
                <Switch>
                    {User.isNotAuthenticated() &&
                    <Route path="/registration" render={(props) => (
                        <Registration {...props}/>)}/>}
                    {User.isAuthenticated() &&
                    <Route path="/editProfile" render={(props) => (
                        <EditProfile {...props}/>)}/>}
                    {User.isAuthenticated() &&
                    <Route path="/matches/:id/" render={(props) => (
                        <MatchDetail {...props} />)}/>}
                    {User.isAuthenticated() &&
                    <Route path="/matches" render={(props) => (
                        <MatchList {...props} />)}/>}
                    {User.isAuthenticated() &&
                    <Route path="/" render={(props) => (
                        <MatchingCandidates {...props} />)}/>}
                    {User.isNotAuthenticated() &&
                    <Route path="/" render={(props) => (
                        <Authentication {...props} updateAuthentication={this.updateAuthentication}/> )}/>}
                </Switch>
            </div>
        );
    }
}

ReactDOM.render(
    <CookiesProvider>
        <I18nextProvider i18n={i18n}>
            <Router>
                <Root/>
            </Router>
        </I18nextProvider>
    </CookiesProvider>
    , document.getElementById('root'));

