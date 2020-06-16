apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus-node-exporter
  namespace: kube-system
  labels:
    app: prometheus-node-exporter
spec:
  selector:
    matchLabels:
      app: prometheus-node-exporter
  replicas: 1
  template:
    metadata:
      name: prometheus-node-exporter
      labels:
        app: prometheus-node-exporter
    spec:
      containers:
      - name: prometheus-node-exporter
        image: prom/node-exporter:v1.0.0
        ports:
        - containerPort: 9100
---
apiVersion: v1
kind: Service
metadata:
  annotations:
    prometheus.io/scrape: 'true'
    prometheus.io/app-metrics: 'true'
    prometheus.io/app-metrics-path: '/metrics'
  name: prometheus-node-exporter
  namespace: kube-system
  labels:
    app: prometheus-node-exporter
spec:
  type: NodePort
  selector:
    app: prometheus-node-exporter
  ports:
  - protocol: TCP
    port: 9100
    targetPort: 9100
    nodePort: 30100
