import React from "react";
import {Link} from "react-router-dom";
import User from "../util/User";
import UserAvatar from "react-user-avatar"

class Navigation extends React.Component {
    updateAuthentication() {
        // If we would store the authentication state in the component's state and reset the state,
        // we would not have to do this.
        this.forceUpdate();
    }

    render() {
        let userNavObj = null;
        if (User.isAuthenticated()) {
            userNavObj =
                <div className="userNavObj">
                    <div className="userAvatar"><UserAvatar size="40" name={User.profile.name}/></div>
                    <div className="userName">{User.profile.name}</div>

                </div>
        }
        return (
            <nav className="navbar navbar-inverse navbar-fixed-top">
                <div className="container">
                    <div className="navbar-header">
                        <button type="button" className="navbar-toggle collapsed" data-toggle="collapse"
                                data-target="#navbar">
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <Link to="/" className="navbar-brand">Fratcher</Link>
                    </div>
                    <div id="navbar" className="collapse navbar-collapse">
                        <ul className="nav navbar-nav">
                            <li><Link to="/matches">Matches</Link></li>

                        </ul>
                        <ul className="nav navbar-nav navbar-right">
                            {User.isNotAuthenticated() &&
                            <li><Link to="/user/login">Login</Link></li>}
                            {User.isAuthenticated() &&
                            userNavObj
                            }
                        </ul>
                    </div>
                </div>
            </nav>
        );
    }
}

export default Navigation;