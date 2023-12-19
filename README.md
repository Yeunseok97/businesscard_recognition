

<h1 align="center"> businesscard_recognition </h1>
<h3 align="center"> 시각장애인용 명함 인식 어플리케이션 </h3>  

</br>

<h2 id="table-of-contents"> 목차</h2>

<details open="open">
  <summary>목차</summary>
  <ol>
    <li><a href="#about-the-project"> ➤ 프로젝트 소개</a></li>
    <li><a href="#requirements"> ➤ Requirements</a></li>
    <li><a href="#folder-structure"> ➤ 폴더 구조</a></li>
    <li><a href="#dataset"> ➤ Dataset</a></li>
    <li><a href="#pipeline"> ➤ Pipeline</a></li>
    <li><a href="#augmentation"> ➤ Augmentation</a></li>
  </ol>
</details>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="about-the-project"> 프로젝트 소개</h2>

<p align="justify"> 
  시각장애인들을 위한 명함 인식,  저장 자동화 어플리케이션입니다. 명함 사진에서 이름과 번호, 직책을 추출하여 전화번호부에 저장합니다. 시각장애인들의 연락처 교환 방식에 도움을 줌으로써 시각장애인들의 사회생활 참여 독려를 목표로 합니다.
</p>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="requirements"> Requirements</h2>

[![made-with-python](https://img.shields.io/badge/Made%20with-Python-1f425f.svg)](https://www.python.org/) <br>
[![Made withJupyter](https://img.shields.io/badge/Made%20with-Jupyter-orange?style=for-the-badge&logo=Jupyter)](https://jupyter.org/try) <br>

프로젝트를 사용하기 위한 Library version입니다.
* Numpy>=1.21.6
* Pandas>=1.3.5
* tqdm>=4.66.1
* torch>=1.13.1
* ultralytics>=8.0.145
* easyocr>=1.7.1
* pororo>=0.4.2

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="folder-structure"> 폴더 구조</h2>

    code
    .
    │
    ├── data
    │   ├── raw_data
    │   │   ├── phone
    │   │   │   ├── accel
    │   │   │   └── gyro
    │   │   ├── watch
    │   │       ├── accel
    │   │       └── gyro
    │   │
    │   ├── transformed_data
    │   │   ├── phone
    │   │   │   ├── accel
    │   │   │   └── gyro
    │   │   ├── watch
    │   │       ├── accel
    │   │       └── gyro
    │   │
    │   ├── feature_label_tables
    │   │    ├── feature_phone_accel
    │   │    ├── feature_phone_gyro
    │   │    ├── feature_watch_accel
    │   │    ├── feature_watch_gyro
    │   │
    │   ├── wisdm-dataset
    │        ├── raw
    │        │   ├── phone
    │        │   ├── accel
    │        │   └── gyro
    │        ├── watch
    │            ├── accel
    │            └── gyro
    │
    ├── CNN_Impersonal_TransformedData.ipynb
    ├── CNN_Personal_TransformedData.ipynb  
    ├── CNN_Impersonal_RawData.ipynb    
    ├── CNN_Personal_RawData.ipynb 
    ├── Classifier_SVM_Personal.ipynb
    ├── Classifier_SVM_Impersonal.ipynb
    ├── statistical_analysis_time_domain.py
    ├── Topological data analysis.ipynb  

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="dataset"> Dataset</h2>
<p> 
 직접 촬영한 명함 1500장의 Image와 명함에서 이름, 번호, 직책의 Bounding Box를 직접 라벨링한 Text파일로 Raw dataset을 구성했습니다.  명함의 디자인이 다양한 만큼 최대한 다양한 명함을 수집하여 데이터셋을 구성하였고, 이름, 번호, 직책의 수를 최대한 균등하게 맞추어 구성하였습니다.

<p align="center">
  <img src="images/businesscard.jpg" alt="businesscard.jpg" display="inline-block" width="60%" height="50%">
</p>

 
 아래 표는 데이터셋에 존재하는 label의 갯수입니다.
</p>

<p align="center">
  <img src="images/LabelTable.png" alt="Table1: 3 label" width="45%" height="45%">
</p>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="pipeline"> Pipeline</h2>

<p align="justify"> 
  전체적인 어플리케이션의 작동 파이프라인입니다. 명함 인식 과정은 두 단계에 걸쳐 진행됩니다. 인식 정확도와 실시간 작동 속도를 높이기 위해 단순 OCR이 아닌 Detection > OCR 두 단계에 걸쳐 진행하였습니다.
<ol>
  <li>
    <p align="justify"> 
      인식할 사진 선택 - 이미지 촬영, 불러오기 두가지 방법으로 인식을 진행할 이미지를 받아옵니다. 시각장애인의 사용 편의성을 높이기 위해 촬영 시 핸드폰 조도센서를 사용하여 어두운 환경에 대한 안내를 제공합니다.
    </p>
  </li>
  <li>
    <p align="justify"> 
      YOLO V5 information detection - 직접 학습시킨 명함 정보 Detection 모델을 사용해서 입력받은 명함에서 이름, 번호, 직책의 Bounding box를 추출하여 개별적인 이미지로 받아옵니다.
    </p>
  </li>
  <li>
    <p align="justify"> 
      Easy OCR information recognition - Easy OCR 모델을 사용하여 추출된 이미지에서 정보를 인식합니다. 이름 또는 번호의 인식이 실패했거나 형식에 맞지 않는 경우 인식 실패에 대한 안내를 제공합니다.
    </p>
  </li>
    <li>
    <p align="justify"> 
      전화번호부 저장 - 인식이 성공적으로 진행된 경우, 인식된 정보의 안내를 제공한 후 전화번호부 저장을 진행합니다.
    </p>
  </li>
</ol>
</p>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="augmentation"> Augmentation</h2>

<p align="justify"> 
  Detection 모델의 성능 증대를 위한 2차 학습을 위해 Data augmentation을 진행하였습니다. Augmentation은 성능 향상에 도움이 될 수 있을만한 2가지 방법을 사용하였습니다.
  <ol>
    <li>180도 Rotation  - Raw data를 180도 회전시켜 Augmentation을 진행하였습니다. 이 방식을 통해 데이터를 증강하여 명함을 뒤집어 잡고 인식시키는 경우에 대한 Detection을 방지하였습니다.</li> 
    <li>명도, 채도, 밝기 조정 - Raw data의 명도, 채도, 밝기를 랜덤하게 조정하여 Augmentation을 진행했습니다. 이 방식을 통해 다양한 밝기의 환경, 촬영 과정에서의 빛번짐 등 Detection 과정에서 다양한 시각적 noise를 방지하였습니다.</li>
  </ol>
  

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

<h2 id="result"> Result</h2>

<p align="justify"> 
  Detection 모델의 정확도는 아래 표와 같습니다. 각각의 Label에 대한 mAP값을 계산하였습니다. 
 
<p align="Center">
  <img src="images/model_test.png" alt="result" width="45%" height="45%">
</p>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)
