import axios from "axios";
import React from "react";
import {withCookies} from "react-cookie";

import User from "../util/User";

class Authentication extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            identification: '',
            password: '',
            error: undefined
        };

        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
        this.cookies = this.props.cookies;
    }

    handleEmailChange(event) {
        this.setState({identification: event.target.value});
    }


    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }


    handleSubmit(event) {
        event.preventDefault();
        axios.post('/api/user/login', this.state, {
            // We allow a status code of 401 (unauthorized). Otherwise it is interpreted as an error and we can't
            // check the HTTP status code.
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status == 401
            }
        })
            .then(({data, status}) => {
                switch (status) {
                    case 202:
                        User.setCookieCredentials(data);
                        this.setState({error: undefined});

                        // Store authentication values even after refresh.
                        this.cookies.set('auth', {
                            token: data.token,
                            user: User
                        }, {path: '/'});

                        // Send event of updated login state.
                        this.props.updateAuthentication();

                        // Redirect to front page.
                        this.props.history.push("/");
                        break;

                    case 401:
                        this.setState({error: true});
                        break;
                }
            });
    }

    handleLogout() {
        axios.defaults.headers.common['Authorization'] = undefined;
        User.reset();
        this.cookies.remove('auth');
        this.forceUpdate();
        this.props.updateAuthentication();
    }

    // Template of centered login page is from
    // https://codepen.io/OldManJava/pen/bwupj
    render() {
        let loginComponent = null;
            loginComponent =
                <div className="container">
                    <div className="row">
                        <div className="Absolute-Center is-Responsive">
                            <div id="logo-container"></div>
                            <div className="col-sm-12 col-md-10 col-md-offset-1">
                                <form action="" id="loginForm" onSubmit={this.handleSubmit}>
                                    <div className="form-group input-group">
                                        <span className="input-group-addon"><i className="glyphicon glyphicon-user"></i></span>
                                        <input className="form-control" type="text"
                                               name='username'
                                               placeholder="username or email"
                                               autoFocus={true}
                                               value={this.state.identification}
                                               onChange={this.handleEmailChange}/>
                                    </div>
                                    <div className="form-group input-group">
                                        <span className="input-group-addon"><i className="glyphicon glyphicon-lock"></i></span>
                                        <input className="form-control" type="password"
                                               name='password'
                                               placeholder="password"
                                               value={this.state.password}
                                               onChange={this.handlePasswordChange}/>
                                    </div>
                                    <div className="form-group">
                                        <button type="submit" className="btn btn-def btn-block">Login</button>
                                    </div>
                                    <div className="form-group text-center">
                                        <a href="#">Forgot Password</a>&nbsp;|&nbsp;<a href="#">Register</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
        return (
            <div className="component">
                {loginComponent}

                <p/>
                {this.state.error &&
                <div className="alert alert-danger">
                    Login was not successful.
                </div>
                }
            </div>
        );
    }
}


export default withCookies(Authentication);