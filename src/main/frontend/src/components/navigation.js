import React from "react";

import User from "../util/User";

import {MenuItem, Nav, Navbar, NavDropdown, NavItem} from "react-bootstrap";
import UserAvatar from "react-user-avatar";
import {withCookies} from "react-cookie";

class Navigation extends React.Component {

    constructor(props) {
        super(props);
        console.log("nav");
        console.log(props);
        this.cookies = this.props.cookies;
        this.handleLogout = this.handleLogout.bind(this);
        this.updateAuthentication = this.props.updateAuthentication.bind(this);
    }


    handleLogout() {
        console.log("handleLogout");
        User.reset();
        this.cookies.remove('auth');
        this.updateAuthentication();
        console.log("end");
    }

    render() {
        let navigation = null;
        navigation =
            <Navbar>
                <Navbar.Header>
                    <Navbar.Brand>
                        <a href="#">Fratcher</a>
                    </Navbar.Brand>
                </Navbar.Header>
                <Nav>
                    <NavItem eventKey={1} href="#">Link</NavItem>
                    <NavItem eventKey={2} href="#">Link</NavItem>
                </Nav>
                <Nav pullRight>
                    <NavDropdown eventKey={3} title={
                        <div className="userNavObj">
                            <div className="userAvatar">
                                <UserAvatar size="40" name={User.profile.name}/>
                            </div>
                            <div className="userName">{User.profile.name}</div>
                        </div>} id="basic-nav-dropdown">
                        <MenuItem eventKey={3.1}>Action</MenuItem>
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