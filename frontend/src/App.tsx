import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {Client} from "stompjs";

interface AppState {
    messages: string[] | null,
    name: string,
}

interface AppProps {
}

class App extends Component<AppProps, AppState> {

    state: AppState;

    private client: Client;

    public componentWillMount() {
        this.setState({
            messages: new Array<string>(),
            name: null,
        });
    }

    constructor(props: any) {
        super(props);

        // init stomp, create chanel
        const socket = new SockJS('/ws-main');
        this.client = Stomp.over(socket);
        this.client.connect({},
            (frame) => {
                console.log("Connected", frame);
                const url = socket["_transport"]["url"];
                const sessionId = url.split('/')[5];
                console.log("SessionId", sessionId);

                this.client.subscribe(`/topic/u-${sessionId}`, (msg) => {
                    this.onMessage(msg);
                });

            }
        );

    }

    onMessage(msg: any): void {
        console.log(msg);
    }

    render() {
        const {messages, name} = this.state;

        const chatLayout =
            <div id="ChatLayout">
                <div id="w-header" className="widget">
                    header
                </div>
                <div id="w-messages" className="widget">
                    messages {messages}
                </div>
                <div id="w-users" className="widget">
                    users
                </div>
                <div id="w-input" className="widget">
                    input
                </div>
            </div>;

        const setNameLayout =
            <div>
                first you need to set your name: &nbsp;
                <input/>
                <button>Set</button>
            </div>;

        if (name === null) {
            return setNameLayout;
        } else {
            return chatLayout;
        }
    }
}

export default App;

