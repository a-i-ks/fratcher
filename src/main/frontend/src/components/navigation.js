import React from "react";

import User from "../util/User";

import {MenuItem, Nav, Navbar, NavDropdown, NavItem} from "react-bootstrap";
import UserAvatar from "react-user-avatar";
import {withCookies} from "react-cookie";

class Navigation extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            name: User.profile.name === null ? "Unknown" : User.profile.name
        };
        this.cookies = this.props.cookies;
        this.handleLogout = this.handleLogout.bind(this);
        this.updateAuthentication = this.props.updateAuthentication.bind(this);
    }


    handleLogout() {
        User.reset();
        this.cookies.remove('auth');
        this.updateAuthentication();
    }

    render() {
        let navigation =
            <Navbar>
                <Navbar.Header>
                    <Navbar.Brand>
                        <a href="#">Fratcher</a>
                    </Navbar.Brand>
                </Navbar.Header>
                <Nav>
                    <NavItem eventKey={1} href="#">Matches</NavItem>
                    {User.isMod() || User.isAdmin() &&
                    <NavItem eventKey={2} href="#">Moderation</NavItem>}
                    {User.isAdmin() &&
                    <NavItem eventKey={3} href="#">Administration</NavItem>
                    }
                </Nav>
                <Nav pullRight>
                    <NavDropdown eventKey={3} title={
                        <div className="userNavObj">
                            <div className="userAvatar">
                                <UserAvatar size="40" name={this.state.name}/>
                            </div>
                            <div className="userName">{this.state.name}</div>
                        </div>} id="basic-nav-dropdown">
                        <MenuItem href="#/editProfile">Edit Profile</MenuItem>
                        <MenuItem eventKey={3.2}>Another action</MenuItem>
                        <MenuItem eventKey={3.3}>Something else here</MenuItem>
                        <MenuItem divider/>
                        <MenuItem onClick={this.handleLogout}><span style={{color: `red`}}>Logout</span></MenuItem>
                    </NavDropdown>
                </Nav>
            </Navbar>;
        return (
            navigation
        );
    }
}

export default withCookies(Navigation);