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

        // init stomp
        /*        const socket = new SockJS('/ws-main'); // http://localhost:8080/ws-main
                this.client = Stomp.over(socket);
                this.client.connect({},
                    (frame) => {
                        console.log("Connected", frame);
                        var sessionId = /\/([^\/]+)\/websocket/.exec(socket["_transport"]["url"])[1];
                        console.log("SessionId", sessionId);
                        this.client.subscribe("/topic/greetings", (greeting) => {
                            console.log(greeting);
                            const msg = this.state["messages"];
                            msg.push(JSON.parse(greeting.body).content);
                            this.setState({messages: msg,});
                        });

                        this.client.subscribe(`/topic/u-${sessionId}`, (msg) => {
                            console.log(msg);
                        });

                        this.client.send("/app/hello", {}, JSON.stringify({'name': 'The name'}));
                    }
                );*/

    }


    render() {
        const {messages} = this.state;

        return (
            <div className="App">
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
            </div>
        );
    }
}

export default App;

