# open_api
IoT SW platform for development

1. application
2. web server
3. IoT

BluetoothAdapter()
 - 블루투스 연결을 위한 주변 블루투스 목록, 혹은 블루투스 연결 시도시 연결 가능여부 확인 

FileWrite(String command)
 - log파일을 저장하기 위한 함수

FileWriteText(String file_path, String file_name, String contents)
 - 폴더, 파일 존재 여부 판단후 폴더 및 파일 생성
 - smart Device 내의 path를 절대경로로 하여 생성

getPairedDevices()
 - 블루투스 연결 가용

Connection(BluetoothDevice device, String UUID_name)
 - clustering 된 목록에서 기능 UUID 출력 및 샐행 확인
 - UUID목록은 uuid_collect확인

doInBackground(void... param)
 - 블루투스 통신을 가능하게 하기 위한 소택 연결
 - 소켓의 data교환을 위한 stream
 - 주기적인 통신상태 전달

onPostExecute(Object result)
 - 블루투스의 상태 출력 및 에러상황 확인

CloseTask()
 - stream 연결 해제

DeviceDialog()
 - Device의 상태를 출력

ErrorDialog()
 - Device의 에러상태 출력

beginListenData()
 - Thread환경 구성
 - Data buffer의 상태, 위치표시
 - web과의 통신을 위한 Byte array로 변환 후 저장
 - 포인터인 buffer위치를 byte로 encoding

sendData()
 - 블루투스로 app의 data(command)
 
RecognitionListener()
 - 음성 기능 권한 획득
 - onReadyForSpeech
 	- 권환 획득
 - onBeginningOfSpeech
 	- speech시작
 - onRmsChanged
 	- manifest의 권한 미해결시 강제권한 실행
 - onBufferReceived
 	- speech의 내용반환
 - onEndOfSpeech
 	- speech종료시 close
 - onError
 	- speech에러시 출력
 - onResults(String title_cmd, String sub_cmd_1, String sub_cmd_2, int able_time)
 	- 스피치 처리함수
	- title_cmd : 불, TV 등 동작module 키워드
	- sub_cmd_no : 켜, 꺼 등 기능 키워드
	- 스피치 시간 able_time으로 한정

fileScan(String path)
 - path의 file을 확인후 일정용량 이상의 text파일일 경우 scan byte로 읽기
 
AndroidBridge
 - java환경에서의 Data를 html,js,jsp등 web환경과 통신
 - onKeyDown
 	- back key의 경우 를 따로 처리하여 stop현상을 막음
 - webViewClient
 	- 지정된 URL을 화면에 띄우는 view function


