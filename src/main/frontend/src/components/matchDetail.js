import React from "react";

import User from "../util/User";
import InterestsTagCloud from "./interestsTagCloud"


import axios from "axios";
import UserAvatar from "react-user-avatar";
import Chat from "./chat";


class MatchDetail extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matchIdFromPath: props.match.params.id,
            match: null,
            loading: true,
            error: null
        };
        // this.handleClick = this.handleClick.bind(this);
    }

    renderLoading() {
        return <div>Loading...</div>;
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios('/api/match/' + this.state.matchIdFromPath, {
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status === 401
            }
        })
            .then(({data, status}) => {
                if (status === 401) {
                    throw new Error(data);
                }
                let user = null;
                const {user1, user2} = data;
                if (user1.id === User.id) {
                    user = user2;
                } else {
                    user = user1;
                }

                this.setState({
                    match: data,
                    user: user,
                    loading: false,
                    error: null
                });
                this.forceUpdate();
            })
            .catch((err) => {
                console.error("ERROR during load of match details: ");
                console.error(err.message);
                this.setState({
                    loading: false,
                    error: err.message
                });
                this.forceUpdate();
            });
    }

    renderError() {
        return (
            <div style={{color: 'red'}}>
                An error has occurred: {this.state.error}
            </div>
        );
    }

    renderMatch() {
        // Destructuring error and matches from state.
        // => I don't have to write "this.state...." every time.
        const {error, user} = this.state;

        if (error) {
            return this.renderError();
        }



        return (
            <div>
                <div>
                    {user.profile.imgPath &&
                    <UserAvatar size="100" name={user.profile.name} src={user.profile.imgPath}/>}
                    {!user.profile.imgPath &&
                    <UserAvatar size="40" name={user.profile.name}/>}
                </div>
                <h1>{user.profile.name}</h1>
                <div>{user.profile.aboutMe}</div>
                <div>
                    <InterestsTagCloud data={user.profile.interests}/>
                </div>
            </div>
        );
    }

    render() {
        const {loading, error} = this.state;
        return (
            <div className="component">
                {loading ? this.renderLoading() :
                    error ? this.renderError() :
                        <div className="row">
                            <div className="col-sm-6">{this.renderMatch()}</div>
                            <div className="col-sm-6">Chat
                                <Chat match={this.state.match}/>
                            </div>
                        </div>}
            </div>)
    }
}

export default MatchDetail;