import React from "react";
import {CookiesProvider} from "react-cookie";
import ReactDOM from "react-dom";

import {I18nextProvider} from "react-i18next";
import {HashRouter as Router, Route, Switch} from "react-router-dom";
import Authentication from "./components/authentication";
import Navigation from "./components/navigation"
import Registration from "./components/registration"

import i18n from "./i18n";
import User from "./util/User";


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
            <Switch>
                {User.isNotAuthenticated() &&
                <Route path="/registration" render={(props) => (
                    <Registration {...props}/>)}/>}
                {User.isAuthenticated() &&
                <Navigation updateAuthentication={this.updateAuthentication} ref={(component) => {
                    this.nav = component;
                }}/>
                }
                {User.isNotAuthenticated() &&
                <Route path="/" render={(props) => (
                    <Authentication {...props} updateAuthentication={this.updateAuthentication}/> )}/>}
            </Switch>
        );
    }
};

ReactDOM.render(
    <CookiesProvider>
        <I18nextProvider i18n={i18n}>
            <Router>
                <Root/>
            </Router>
        </I18nextProvider>
    </CookiesProvider>
    , document.getElementById('root'));

