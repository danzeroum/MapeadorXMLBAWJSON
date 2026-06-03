# 🚀 Sistema de Análise IBM BAW V3

## 📋 Visão Geral

O Sistema de Análise IBM BAW V3 é uma ferramenta completa para análise e documentação de processos IBM Business Automation Workflow. Desenvolvido especificamente para otimizar a interpretabilidade por IA, o sistema gera relatórios detalhados de qualidade, métricas executivas e validação estrutural.

### 🎯 **Principais Funcionalidades**

- ✅ **Análise Estrutural Completa** - Processa BPMNs modernos e legados
- ✅ **Relatórios de Qualidade para IA** - Detecta problemas de interpretabilidade
- ✅ **Validação BPMN** - Verifica estrutura e conformidade
- ✅ **Métricas Executivas** - Relatórios gerenciais e de complexidade
- ✅ **Múltiplos Formatos de Saída** - JSON, TXT, HTML
- ✅ **Logging Avançado** - Monitoramento e debugging detalhado
- ✅ **Arquitetura Modular** - Fácil extensão e manutenção

## 🏗️ **Arquitetura do Sistema**

### **Serviços Core**
```
📦 Core Services
├── 🔍 DependencyExtractorService     # Extração de dependências
├── 🛤️  ExecutionPathGeneratorService  # Geração de caminhos de execução
├── 🌳 RootViewBuilderService         # Construção de visão hierárquica
└── 💎 VariableEnricherService        # Enriquecimento de variáveis
```

### **Processadores Especializados**
```
📦 Processors
├── 🔄 BpmnProcessorService           # Processos BPMN modernos
├── 🏛️  TeamworksProcessorService      # Serviços legados Teamworks
├── 📊 BpdProcessorService            # BPDs legados
├── 🏢 BusinessObjectProcessorService # Objetos de negócio (TwClass)
└── 🖥️  UiProcessorService             # Interface de usuário (Coach Views)
```

### **Análise e Qualidade**
```
📦 Analysis & Quality
├── 🔍 QualityReportGeneratorService  # Relatórios de qualidade para IA
├── ✅ BpmnStructureValidatorService  # Validação estrutural BPMN
└── 📈 ProcessMetricsService          # Métricas e relatórios executivos
```

### **Configuração e Utilitários**
```
📦 Configuration & Utils
├── ⚙️  BawAnalysisConfig             # Configurações centralizadas
├── 🔧 AnalysisConfig                 # Builder para configuração
├── 📝 AnalysisLogger                 # Sistema de logging avançado
└── 🛠️  BawAnalysisUtils              # Métodos utilitários
```

## 🚀 **Instalação e Configuração**

### **Pré-requisitos**
- ☕ Java 8 ou superior
- 🏗️ Maven ou Gradle (opcional, para dependências)
- 💾 Pelo menos 2GB de RAM disponível
- 📁 Acesso aos arquivos `.twx` extraídos do IBM BAW

### **Instalação Rápida**

1. **Clone ou baixe o código fonte**
```bash
# Estrutura de diretórios necessária
mkdir ibm-baw-analyzer-v3
cd ibm-baw-analyzer-v3
mkdir -p src lib output
```

2. **Configure as dependências**
```bash
# Coloque as JARs necessárias em lib/
cp /path/to/your/jars/*.jar lib/
```

3. **Compile e execute**
```bash
# Torne o script executável
chmod +x build_and_run.sh

# Execute o build completo
./build_and_run.sh all
```

## 📖 **Guia de Uso**

### **1. Execução Básica**

#### **Modo Interativo (Recomendado)**
```bash
./build_and_run.sh run
```

#### **Linha de Comando**
```bash
java -jar dist/baw-analyzer-v3.jar "MeuProjeto" "1.abc123" "/path/to/twx"
```

#### **Programático (Java)**
```java
AnalysisConfig config = AnalysisConfig.builder()
    .projectName("MeuProjeto")
    .processId("1.processo-id")
    .extractionPath("/path/to/twx")
    .outputFileName("analise_" + timestamp + ".json")
    .enableDetailedLogging(true)
    .build();

ImprovedBawAnalysisMain.executeJsonReportGeneration(config);
```

### **2. Configurações Avançadas**

#### **Configuração Completa**
```java
AnalysisConfig config = AnalysisConfig.builder()
    .projectName("MeuProcessoBPM")
    .processId("<SEU-PROCESS-ID>")
    .activityName("<NOME-DO-PROCESSO>")
    .extractionPath("C:\\SeuCaminho\\SeuProjeto")
    .outputFileName("processo_v3.json")
    .outputDirectory("output")
    .rootViewDepth(2)                    // Profundidade de subprocessos
    .max